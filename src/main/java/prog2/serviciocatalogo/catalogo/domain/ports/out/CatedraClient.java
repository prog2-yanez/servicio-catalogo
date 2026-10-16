package prog2.serviciocatalogo.catalogo.domain.ports.out;

import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;

public interface CatedraClient {

    SnapshotCatalogo obtenerSnapshot();
}
