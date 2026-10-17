package prog2.serviciocatalogo.catalogo.domain.ports.in;

import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;

public interface AplicarSnapshotUseCase {

    void aplicar(SnapshotCatalogo snapshot);
}
