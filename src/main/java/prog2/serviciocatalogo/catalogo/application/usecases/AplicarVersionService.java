package prog2.serviciocatalogo.catalogo.application.usecases;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.model.ActualizacionDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

// Aplica una sola versión. Está en una clase aparte para que Spring abra una transacción por cada versión.
@Service
public class AplicarVersionService {

    private final CatalogoRepository catalogoRepository;

    public AplicarVersionService(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    // Los cambios y la versión se guardan juntos: si algo falla, la versión local no avanza.
    @Transactional
    public void aplicar(ActualizacionDeVersion actualizacion) {
        catalogoRepository.guardarCambios(actualizacion);
        catalogoRepository.guardarVersion(new VersionCatalogo(actualizacion.version(), Instant.now()));
    }
}
