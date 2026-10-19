package prog2.serviciocatalogo.catalogo.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraException;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.InicializarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;

@Service
public class InicializarCatalogoService implements InicializarCatalogoUseCase {

    private static final Logger log = LoggerFactory.getLogger(InicializarCatalogoService.class);

    private final CatalogoRepository catalogoRepository;
    private final CatedraClient catedraClient;
    private final AplicarSnapshotUseCase aplicarSnapshot;

    public InicializarCatalogoService(
            CatalogoRepository catalogoRepository,
            CatedraClient catedraClient,
            AplicarSnapshotUseCase aplicarSnapshot) {
        this.catalogoRepository = catalogoRepository;
        this.catedraClient = catedraClient;
        this.aplicarSnapshot = aplicarSnapshot;
    }

    // Sin versión local no hay catálogo consistente, porque el snapshot y la versión se guardan juntos.
    @Override
    public void inicializarSiEstaVacio() {
        if (catalogoRepository.obtenerVersion().isPresent()) {
            return;
        }
        try {
            aplicarSnapshot.aplicar(catedraClient.obtenerSnapshot());
            log.info("Catálogo inicializado con el snapshot de la cátedra");
        } catch (CatedraException e) {
            log.warn("El catálogo no tiene datos: no se pudo obtener el snapshot ({})", e.getMessage());
        }
    }
}
