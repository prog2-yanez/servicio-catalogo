package prog2.serviciocatalogo.catalogo.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.SincronizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaEventoProcesadoRepository;

// Kafka corre embebido en la prueba y la base es real. La sincronización se simula: lo que se prueba
// es cuántas veces se la llama por cada aviso.
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
        "catedra.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "catedra.kafka.consumer-group=grupo-de-prueba",
        "catedra.kafka.topic-catalogo=" + CatalogoActualizadoKafkaTests.TOPIC
})
@EmbeddedKafka(partitions = 1, topics = CatalogoActualizadoKafkaTests.TOPIC)
class CatalogoActualizadoKafkaTests {

    static final String TOPIC = "catalogo-de-prueba";

    private static final Duration ESPERA = Duration.ofSeconds(20);

    @Autowired
    private KafkaTemplate<String, String> kafka;

    @Autowired
    private JpaEventoProcesadoRepository eventosProcesados;

    @MockitoBean
    private SincronizarCatalogoUseCase sincronizarCatalogo;

    @Test
    void elMismoEventoRecibidoDosVecesSincronizaUnaSolaVez() {
        when(sincronizarCatalogo.sincronizar()).thenReturn(ResultadoActualizacion.ACTUALIZADO);
        String repetido = UUID.randomUUID().toString();
        String siguiente = UUID.randomUUID().toString();

        publicar(repetido, 8);
        publicar(repetido, 8);
        // Con una sola partición los mensajes llegan en orden: cuando se procesa este, el repetido ya pasó.
        publicar(siguiente, 9);

        await().atMost(ESPERA).until(() -> eventosProcesados.existsById(siguiente));
        assertThat(eventosProcesados.existsById(repetido)).isTrue();
        verify(sincronizarCatalogo, times(2)).sincronizar();
    }

    @Test
    void siLaSincronizacionFallaSeReintentaYElEventoSeRegistraRecienCuandoSaleBien() {
        when(sincronizarCatalogo.sincronizar())
                .thenThrow(new CatedraNoDisponibleException("Redis caido", null))
                .thenReturn(ResultadoActualizacion.ACTUALIZADO);
        String eventId = UUID.randomUUID().toString();

        publicar(eventId, 10);

        await().atMost(ESPERA).until(() -> eventosProcesados.existsById(eventId));
        verify(sincronizarCatalogo, times(2)).sincronizar();
    }

    private void publicar(String eventId, long version) {
        kafka.send(TOPIC, String.valueOf(version), """
                {"eventId": "%s", "eventType": "CatalogUpdated", "newVersion": %d,
                 "occurredAt": "2026-10-31T12:00:00Z", "source": "catedra", "schemaVersion": 1}
                """.formatted(eventId, version)).join();
    }
}
