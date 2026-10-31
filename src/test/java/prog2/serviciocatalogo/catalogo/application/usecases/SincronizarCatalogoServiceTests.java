package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ActualizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ReconstruirCatalogoUseCase;

class SincronizarCatalogoServiceTests {

    private final ActualizarCatalogoUseCase actualizarCatalogo = mock(ActualizarCatalogoUseCase.class);
    private final ReconstruirCatalogoUseCase reconstruirCatalogo = mock(ReconstruirCatalogoUseCase.class);
    private final SincronizarCatalogoService servicio =
            new SincronizarCatalogoService(actualizarCatalogo, reconstruirCatalogo);

    @ParameterizedTest
    @EnumSource(value = ResultadoActualizacion.class, names = {"AL_DIA", "ACTUALIZADO"})
    void siSePudoActualizarNoReconstruye(ResultadoActualizacion resultado) {
        when(actualizarCatalogo.actualizar()).thenReturn(resultado);

        assertThat(servicio.sincronizar()).isEqualTo(resultado);

        verifyNoInteractions(reconstruirCatalogo);
    }

    @Test
    void siNoSePuedeActualizarReconstruyeConElSnapshot() {
        when(actualizarCatalogo.actualizar()).thenReturn(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);

        assertThat(servicio.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verify(reconstruirCatalogo).reconstruir();
    }

    @Test
    void siLaReconstruccionFallaPropagaLaExcepcion() {
        when(actualizarCatalogo.actualizar()).thenReturn(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);
        doThrow(new CatedraNoDisponibleException("HTTP 503", null)).when(reconstruirCatalogo).reconstruir();

        assertThatThrownBy(servicio::sincronizar).isInstanceOf(CatedraNoDisponibleException.class);
    }

    @Test
    void siLaActualizacionFallaNoIntentaReconstruir() {
        when(actualizarCatalogo.actualizar()).thenThrow(new IllegalStateException("Redis no responde"));

        assertThatThrownBy(servicio::sincronizar).isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(reconstruirCatalogo);
    }
}
