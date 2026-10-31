package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.ports.out.EventosProcesados;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaEventoProcesadoRepository;

@Component
public class EventosProcesadosPersistenceAdapter implements EventosProcesados {

    private final JpaEventoProcesadoRepository repository;

    public EventosProcesadosPersistenceAdapter(JpaEventoProcesadoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean yaFueProcesado(String eventId) {
        return repository.existsById(eventId);
    }

    @Override
    @Transactional
    public boolean registrar(String eventId, String tipo) {
        return repository.insertarSiNoExiste(eventId, tipo, Instant.now()) == 1;
    }
}
