package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.AvisoCatalogoActualizado;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.SincronizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.EventosProcesados;

class ProcesarCatalogoActualizadoServiceTests {

    private static final AvisoCatalogoActualizado AVISO = new AvisoCatalogoActualizado("evento-1", 8);

    private final EventosProcesados eventosProcesados = mock(EventosProcesados.class);
    private final SincronizarCatalogoUseCase sincronizarCatalogo = mock(SincronizarCatalogoUseCase.class);
    private final ProcesarCatalogoActualizadoService servicio =
            new ProcesarCatalogoActualizadoService(eventosProcesados, sincronizarCatalogo);

    @Test
    void unAvisoNuevoSincronizaYDespuesQuedaRegistrado() {
        when(sincronizarCatalogo.sincronizar()).thenReturn(ResultadoActualizacion.ACTUALIZADO);

        assertThat(servicio.procesar(AVISO)).isTrue();

        InOrder orden = inOrder(sincronizarCatalogo, eventosProcesados);
        orden.verify(sincronizarCatalogo).sincronizar();
        orden.verify(eventosProcesados).registrar("evento-1", "CatalogUpdated");
    }

    @Test
    void unAvisoRepetidoSeDescartaSinSincronizar() {
        when(eventosProcesados.yaFueProcesado("evento-1")).thenReturn(true);

        assertThat(servicio.procesar(AVISO)).isFalse();

        verifyNoInteractions(sincronizarCatalogo);
        verify(eventosProcesados, never()).registrar(anyString(), anyString());
    }

    @Test
    void siLaSincronizacionFallaNoLoRegistraParaQueSeReintente() {
        when(sincronizarCatalogo.sincronizar()).thenThrow(new CatedraNoDisponibleException("Redis caido", null));

        assertThatThrownBy(() -> servicio.procesar(AVISO)).isInstanceOf(CatedraNoDisponibleException.class);

        verify(eventosProcesados, never()).registrar(anyString(), anyString());
    }
}
