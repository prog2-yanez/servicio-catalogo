package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

class ReconstruirCatalogoServiceTests {

    private static final SnapshotCatalogo SNAPSHOT = new SnapshotCatalogo(
            3, Instant.parse("2026-09-10T13:40:00Z"), List.of(), List.of(), List.of());

    private final CatedraClient catedraClient = mock(CatedraClient.class);
    private final AplicarSnapshotUseCase aplicarSnapshot = mock(AplicarSnapshotUseCase.class);
    private final RegistroSincronizacion registroSincronizacion = mock(RegistroSincronizacion.class);
    private final ReconstruirCatalogoService servicio = new ReconstruirCatalogoService(
            catedraClient, aplicarSnapshot, registroSincronizacion);

    @Test
    void pideElSnapshotLoAplicaYQuedaSincronizado() {
        when(catedraClient.obtenerSnapshot()).thenReturn(SNAPSHOT);

        servicio.reconstruir();

        InOrder orden = inOrder(registroSincronizacion, aplicarSnapshot);
        orden.verify(registroSincronizacion).marcarReconstruyendo();
        orden.verify(aplicarSnapshot).aplicar(SNAPSHOT);
        orden.verify(registroSincronizacion).marcarSincronizado();
    }

    @Test
    void siLaCatedraFallaMarcaErrorYPropagaSinTocarLaCopia() {
        when(catedraClient.obtenerSnapshot()).thenThrow(new CatedraNoDisponibleException("HTTP 503", null));

        assertThatThrownBy(servicio::reconstruir).isInstanceOf(CatedraNoDisponibleException.class);

        verify(aplicarSnapshot, never()).aplicar(any());
        verify(registroSincronizacion).marcarError();
        verify(registroSincronizacion, never()).marcarSincronizado();
    }

    @Test
    void siFallaAlGuardarMarcaErrorYPropaga() {
        when(catedraClient.obtenerSnapshot()).thenReturn(SNAPSHOT);
        doThrow(new IllegalStateException("la base no responde")).when(aplicarSnapshot).aplicar(SNAPSHOT);

        assertThatThrownBy(servicio::reconstruir).isInstanceOf(IllegalStateException.class);

        verify(registroSincronizacion).marcarError();
        verify(registroSincronizacion, never()).marcarSincronizado();
    }
}
