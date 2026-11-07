package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaEventoProcesadoRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, EventosProcesadosPersistenceAdapter.class})
class EventosProcesadosPersistenciaTests {

    private static final String TIPO = "CatalogUpdated";

    @Autowired
    private EventosProcesadosPersistenceAdapter eventosProcesados;

    @Autowired
    private JpaEventoProcesadoRepository repository;

    @Test
    void unEventoNuevoNoFueProcesado() {
        assertThat(eventosProcesados.yaFueProcesado("evento-nuevo")).isFalse();
    }

    @Test
    void despuesDeRegistrarloQuedaComoProcesado() {
        assertThat(eventosProcesados.registrar("evento-1", TIPO)).isTrue();

        assertThat(eventosProcesados.yaFueProcesado("evento-1")).isTrue();
        assertThat(repository.findById("evento-1")).hasValueSatisfying(evento -> {
            assertThat(evento.getTipo()).isEqualTo(TIPO);
            assertThat(evento.getProcesadoEn()).isNotNull();
        });
    }

    @Test
    void registrarDosVecesElMismoEventoNoFallaYGuardaUnaSolaFila() {
        assertThat(eventosProcesados.registrar("evento-2", TIPO)).isTrue();
        assertThat(eventosProcesados.registrar("evento-2", TIPO)).isFalse();

        assertThat(repository.findAll()).filteredOn(evento -> evento.getEventId().equals("evento-2")).hasSize(1);
    }
}
