package prog2.serviciocatalogo.catalogo.domain.ports.out;

// Registro de los eventos de Kafka ya procesados, para no procesar dos veces el mismo.
public interface EventosProcesados {

    boolean yaFueProcesado(String eventId);

    // Devuelve false si el evento ya estaba registrado.
    boolean registrar(String eventId, String tipo);
}
