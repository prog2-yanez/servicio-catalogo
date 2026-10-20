package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;

class InicializarCatalogoServiceTests {

    private static final Instant GENERADO = Instant.parse("2026-09-10T13:40:00Z");

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final CatedraClient catedraClient = mock(CatedraClient.class);
    private final AplicarSnapshotUseCase aplicarSnapshot = mock(AplicarSnapshotUseCase.class);
    private final InicializarCatalogoService servicio =
            new InicializarCatalogoService(catalogoRepository, catedraClient, aplicarSnapshot);

    @Test
    void conLaBaseVaciaPideYAplicaElSnapshot() {
        SnapshotCatalogo snapshot = new SnapshotCatalogo(3, GENERADO, List.of(), List.of(), List.of());
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        when(catedraClient.obtenerSnapshot()).thenReturn(snapshot);

        servicio.inicializarSiEstaVacio();

        verify(aplicarSnapshot).aplicar(snapshot);
    }

    @Test
    void conDatosNoVuelveAPedirElSnapshot() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.of(new VersionCatalogo(3, GENERADO)));

        servicio.inicializarSiEstaVacio();

        verifyNoInteractions(catedraClient, aplicarSnapshot);
    }

    @Test
    void siLaCatedraFallaNoAplicaNadaYElServicioSigue() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        when(catedraClient.obtenerSnapshot()).thenThrow(new CatedraNoDisponibleException("HTTP 503", null));

        assertThatCode(servicio::inicializarSiEstaVacio).doesNotThrowAnyException();

        verify(aplicarSnapshot, never()).aplicar(any());
    }
}
