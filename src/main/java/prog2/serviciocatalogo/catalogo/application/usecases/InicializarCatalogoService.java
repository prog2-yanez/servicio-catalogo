package prog2.serviciocatalogo.catalogo.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraException;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.InicializarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

@Service
public class InicializarCatalogoService implements InicializarCatalogoUseCase {

    private static final Logger log = LoggerFactory.getLogger(InicializarCatalogoService.class);

    private final CatalogoRepository catalogoRepository;
    private final CatedraClient catedraClient;
    private final AplicarSnapshotUseCase aplicarSnapshot;
    private final RegistroSincronizacion registroSincronizacion;

    public InicializarCatalogoService(
            CatalogoRepository catalogoRepository,
            CatedraClient catedraClient,
            AplicarSnapshotUseCase aplicarSnapshot,
            RegistroSincronizacion registroSincronizacion) {
        this.catalogoRepository = catalogoRepository;
        this.catedraClient = catedraClient;
        this.aplicarSnapshot = aplicarSnapshot;
        this.registroSincronizacion = registroSincronizacion;
    }

    // Sin versión local no hay catálogo consistente, porque el snapshot y la versión se guardan juntos.
    @Override
    public void inicializarSiEstaVacio() {
        if (catalogoRepository.obtenerVersion().isPresent()) {
            return;
        }
        registroSincronizacion.marcarReconstruyendo();
        try {
            aplicarSnapshot.aplicar(catedraClient.obtenerSnapshot());
            registroSincronizacion.marcarSincronizado();
            log.info("Catálogo inicializado con el snapshot de la cátedra");
        } catch (CatedraException e) {
            registroSincronizacion.marcarError();
            log.warn("El catálogo no tiene datos: no se pudo obtener el snapshot ({})", e.getMessage());
        } catch (RuntimeException e) {
            // Cualquier otra falla también deja el estado en error, pero se sigue propagando.
            registroSincronizacion.marcarError();
            throw e;
        }
    }
}
