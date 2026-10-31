package prog2.serviciocatalogo.catalogo.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraException;
import prog2.serviciocatalogo.catalogo.domain.ports.in.InicializarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ReconstruirCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

@Service
public class InicializarCatalogoService implements InicializarCatalogoUseCase {

    private static final Logger log = LoggerFactory.getLogger(InicializarCatalogoService.class);

    private final CatalogoRepository catalogoRepository;
    private final ReconstruirCatalogoUseCase reconstruirCatalogo;

    public InicializarCatalogoService(
            CatalogoRepository catalogoRepository, ReconstruirCatalogoUseCase reconstruirCatalogo) {
        this.catalogoRepository = catalogoRepository;
        this.reconstruirCatalogo = reconstruirCatalogo;
    }

    // Sin versión local no hay catálogo consistente, porque el snapshot y la versión se guardan juntos.
    @Override
    public void inicializarSiEstaVacio() {
        if (catalogoRepository.obtenerVersion().isPresent()) {
            return;
        }
        try {
            reconstruirCatalogo.reconstruir();
            log.info("Catálogo inicializado con el snapshot de la cátedra");
        } catch (CatedraException e) {
            // Si la cátedra no responde el servicio arranca igual, sin datos; las otras fallas se propagan.
            log.warn("El catálogo no tiene datos: no se pudo obtener el snapshot ({})", e.getMessage());
        }
    }
}
