package prog2.serviciocatalogo.catalogo.application.usecases;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

@Service
public class AplicarSnapshotService implements AplicarSnapshotUseCase {

    private final CatalogoRepository catalogoRepository;

    public AplicarSnapshotService(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    // El catálogo y la versión cambian juntos: si algo falla, se conserva la copia anterior.
    @Override
    @Transactional
    public void aplicar(SnapshotCatalogo snapshot) {
        catalogoRepository.reemplazarCatalogo(snapshot);
        catalogoRepository.guardarVersion(new VersionCatalogo(snapshot.version(), Instant.now()));
    }
}
