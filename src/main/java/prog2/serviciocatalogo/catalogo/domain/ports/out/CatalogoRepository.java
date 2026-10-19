package prog2.serviciocatalogo.catalogo.domain.ports.out;

import java.util.Optional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;

public interface CatalogoRepository {

    Optional<VersionCatalogo> obtenerVersion();

    void reemplazarCatalogo(SnapshotCatalogo snapshot);

    void guardarVersion(VersionCatalogo version);
}
