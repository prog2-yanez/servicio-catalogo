package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

// Regla común de las consultas: sin una versión aplicada, el catálogo no está listo para responder.
@Component
class GuardiaCatalogo {

    private final CatalogoRepository catalogoRepository;

    GuardiaCatalogo(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    VersionCatalogo exigirDatos() {
        return catalogoRepository.obtenerVersion().orElseThrow(CatalogoSinDatosException::new);
    }
}
