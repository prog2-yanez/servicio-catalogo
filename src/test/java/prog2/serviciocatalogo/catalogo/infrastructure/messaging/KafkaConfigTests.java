package prog2.serviciocatalogo.catalogo.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.DefaultErrorHandler;

class KafkaConfigTests {

    @Test
    void despuesDeAgotarLosReintentosConfirmaElOffsetParaNoTrabarElTopic() {
        DefaultErrorHandler manejador = new KafkaConfig().manejadorErroresKafka();

        assertThat(manejador.isAckAfterHandle()).isTrue();
    }

    @Test
    void registrarElDescarteNoFalla() {
        ConsumerRecord<String, String> registro = new ConsumerRecord<>("topic", 0, 42L, "8", "{}");

        assertThatCode(() -> KafkaConfig.registrarDescarte(registro, new IllegalStateException("Redis caido")))
                .doesNotThrowAnyException();
    }
}
