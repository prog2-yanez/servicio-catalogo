package prog2.serviciocatalogo.catalogo.infrastructure.messaging;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.AvisoCatalogoActualizado;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ProcesarCatalogoActualizadoUseCase;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

// Escucha los avisos de versión nueva. Solo existe si Kafka está configurado; si no, el servicio
// funciona igual con REST y Redis.
@Component
@ConditionalOnExpression("!'${catedra.kafka.bootstrap-servers:}'.isBlank()"
        + " and !'${catedra.kafka.consumer-group:}'.isBlank()"
        + " and !'${catedra.kafka.topic-catalogo:}'.isBlank()")
public class CatalogoActualizadoListener {

    private static final Logger log = LoggerFactory.getLogger(CatalogoActualizadoListener.class);

    private final ProcesarCatalogoActualizadoUseCase procesarAviso;
    private final JsonMapper json;

    public CatalogoActualizadoListener(ProcesarCatalogoActualizadoUseCase procesarAviso, JsonMapper json) {
        this.procesarAviso = procesarAviso;
        this.json = json;
    }

    // El offset se confirma recién cuando este método termina bien; si tira una excepción, Kafka reintenta.
    @KafkaListener(id = "catalogo-actualizado", topics = "${catedra.kafka.topic-catalogo}")
    public void recibir(String mensaje) {
        leer(mensaje).ifPresent(procesarAviso::procesar);
    }

    // Un mensaje que no se puede leer no se arregla reintentando: se deja en el log y se sigue.
    Optional<AvisoCatalogoActualizado> leer(String mensaje) {
        CatalogUpdatedMensaje leido;
        try {
            leido = json.readValue(mensaje, CatalogUpdatedMensaje.class);
        } catch (JacksonException e) {
            log.warn("Se descarta un mensaje del catálogo que no es un JSON válido");
            return Optional.empty();
        }
        if (leido == null || !AvisoCatalogoActualizado.TIPO.equals(leido.eventType())) {
            log.debug("Se ignora un mensaje que no es {}", AvisoCatalogoActualizado.TIPO);
            return Optional.empty();
        }
        if (leido.eventId() == null || leido.eventId().isBlank() || leido.newVersion() == null) {
            log.warn("Se descarta un {} sin eventId o sin newVersion", AvisoCatalogoActualizado.TIPO);
            return Optional.empty();
        }
        return Optional.of(new AvisoCatalogoActualizado(leido.eventId(), leido.newVersion()));
    }
}
