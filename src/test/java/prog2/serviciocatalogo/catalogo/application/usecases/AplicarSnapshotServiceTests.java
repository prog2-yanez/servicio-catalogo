package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

class AplicarSnapshotServiceTests {

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final AplicarSnapshotService servicio = new AplicarSnapshotService(catalogoRepository);

    @Test
    void reemplazaElCatalogoYDespuesGuardaLaVersionDelSnapshot() {
        SnapshotCatalogo snapshot = new SnapshotCatalogo(
                12, Instant.parse("2026-09-10T13:40:00Z"), List.of(), List.of(), List.of());

        servicio.aplicar(snapshot);

        InOrder orden = inOrder(catalogoRepository);
        orden.verify(catalogoRepository).reemplazarCatalogo(snapshot);
        orden.verify(catalogoRepository).guardarVersion(any());

        ArgumentCaptor<VersionCatalogo> version = ArgumentCaptor.forClass(VersionCatalogo.class);
        verify(catalogoRepository).guardarVersion(version.capture());
        assertThat(version.getValue().numero()).isEqualTo(12);
        assertThat(version.getValue().actualizadoEn()).isNotNull();
    }
}
