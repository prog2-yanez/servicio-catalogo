package prog2.serviciocatalogo.catalogo.domain.ports.out;

import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;

public interface CatalogoRepository {

    void reemplazarCatalogo(SnapshotCatalogo snapshot);

    void guardarVersion(VersionCatalogo version);
}
