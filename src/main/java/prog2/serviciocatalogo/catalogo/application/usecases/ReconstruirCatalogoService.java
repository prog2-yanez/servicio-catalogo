package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ReconstruirCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

// Reemplaza la copia local entera por el snapshot de la cátedra.
@Service
public class ReconstruirCatalogoService implements ReconstruirCatalogoUseCase {

    private final CatedraClient catedraClient;
    private final AplicarSnapshotUseCase aplicarSnapshot;
    private final RegistroSincronizacion registroSincronizacion;

    public ReconstruirCatalogoService(
            CatedraClient catedraClient,
            AplicarSnapshotUseCase aplicarSnapshot,
            RegistroSincronizacion registroSincronizacion) {
        this.catedraClient = catedraClient;
        this.aplicarSnapshot = aplicarSnapshot;
        this.registroSincronizacion = registroSincronizacion;
    }

    // Si algo falla queda el estado en error y se propaga la excepción para que decida quien llamó.
    // Como el snapshot se aplica en una sola transacción, la copia anterior queda intacta.
    @Override
    public void reconstruir() {
        registroSincronizacion.marcarReconstruyendo();
        try {
            aplicarSnapshot.aplicar(catedraClient.obtenerSnapshot());
            registroSincronizacion.marcarSincronizado();
        } catch (RuntimeException e) {
            registroSincronizacion.marcarError();
            throw e;
        }
    }
}
