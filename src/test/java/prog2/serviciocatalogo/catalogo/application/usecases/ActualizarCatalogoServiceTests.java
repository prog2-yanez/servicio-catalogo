package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.ActualizacionDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CambiosCatalogoCatedra;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

class ActualizarCatalogoServiceTests {

    private static final Instant CREADO = Instant.parse("2026-09-01T03:00:00Z");

    private final CatalogoRepository catalogo = mock(CatalogoRepository.class);
    private final CambiosCatalogoCatedra catedra = mock(CambiosCatalogoCatedra.class);
    private final AplicarVersionService aplicarVersion = mock(AplicarVersionService.class);
    private final RegistroSincronizacion registro = mock(RegistroSincronizacion.class);
    private final ActualizarCatalogoService servicio =
            new ActualizarCatalogoService(catalogo, catedra, aplicarVersion, registro);

    @BeforeEach
    void entidadesDeLaCatedra() {
        when(catedra.categoria(107)).thenReturn(Optional.of(
                new CategoriaProfesional(107L, "Ginecologia", "Atencion ginecologica", true, CREADO, CREADO)));
        when(catedra.profesional(204)).thenReturn(Optional.of(
                new Profesional(204L, 107L, "Laura", "Medina", false, CREADO, CREADO)));
        when(catedra.horario(308)).thenReturn(Optional.of(new HorarioSemanal(308L, 204L, DayOfWeek.TUESDAY,
                LocalTime.of(17, 0), LocalTime.of(20, 0), 30, true, CREADO, CREADO)));
    }

    @Test
    void siLaCopiaYaTieneLaUltimaVersionQuedaAlDia() {
        conVersionLocal(7);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(7, 4)));

        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.AL_DIA);
        verify(aplicarVersion, never()).aplicar(any());
        verify(registro).marcarSincronizado();
    }

    @Test
    void aplicaLasVersionesNuevasDeAUnaYEnOrden() {
        conVersionLocal(4);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(7, 4)));
        when(catedra.cambios(anyLong())).thenAnswer(pedido -> Optional.of(cambiosCompletos(pedido.getArgument(0))));

        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.ACTUALIZADO);

        ArgumentCaptor<ActualizacionDeVersion> aplicadas = ArgumentCaptor.forClass(ActualizacionDeVersion.class);
        verify(aplicarVersion, times(3)).aplicar(aplicadas.capture());
        assertThat(aplicadas.getAllValues()).extracting(ActualizacionDeVersion::version).containsExactly(5L, 6L, 7L);
        assertThat(aplicadas.getValue().profesionales()).singleElement()
                .extracting(Profesional::habilitado).isEqualTo(false);
        verify(registro).marcarSincronizado();
    }

    // Ejemplo del contrato: local 3, más antigua 4, actual 7. No se intenta leer changes:4.
    @Test
    void siLaCopiaQuedoMasViejaQueLoQueConservaLaCatedraPideReconstruir() {
        conVersionLocal(3);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(7, 4)));

        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);
        verify(catedra, never()).cambios(anyLong());
        verify(aplicarVersion, never()).aplicar(any());
    }

    @Test
    void siFaltaUnaVersionIntermediaAplicaLasAnterioresYPideReconstruir() {
        conVersionLocal(4);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(7, 4)));
        when(catedra.cambios(5)).thenReturn(Optional.of(cambiosCompletos(5)));
        when(catedra.cambios(6)).thenReturn(Optional.empty());

        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);

        InOrder orden = inOrder(aplicarVersion);
        orden.verify(aplicarVersion).aplicar(any());
        orden.verifyNoMoreInteractions();
        verify(catedra, never()).cambios(7);
    }

    @Test
    void siFaltaUnaEntidadDeLaVersionNoLaAplicaAMedias() {
        conVersionLocal(4);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(5, 4)));
        when(catedra.cambios(5)).thenReturn(Optional.of(
                new CambiosDeVersion(5, Set.of(107L), Set.of(204L, 299L), Set.of(308L))));

        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);
        verify(aplicarVersion, never()).aplicar(any());
    }

    @Test
    void sinCopiaLocalOSinEstadoDeLaCatedraPideReconstruir() {
        when(catalogo.obtenerVersion()).thenReturn(Optional.empty());
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(7, 4)));
        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);

        conVersionLocal(5);
        when(catedra.estadoVersiones()).thenReturn(Optional.empty());
        assertThat(servicio.actualizar()).isEqualTo(ResultadoActualizacion.REQUIERE_RECONSTRUCCION);

        verify(registro, never()).marcarSincronizado();
    }

    @Test
    void siFallaAlAplicarMarcaErrorYRelanzaParaQueSeReintente() {
        conVersionLocal(4);
        when(catedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(5, 4)));
        when(catedra.cambios(5)).thenReturn(Optional.of(cambiosCompletos(5)));
        doThrow(new CatedraNoDisponibleException("Redis caido", null)).when(aplicarVersion).aplicar(any());

        assertThatThrownBy(servicio::actualizar).isInstanceOf(CatedraNoDisponibleException.class);
        verify(registro).marcarError();
        verify(registro, never()).marcarSincronizado();
    }

    private void conVersionLocal(long numero) {
        when(catalogo.obtenerVersion()).thenReturn(Optional.of(new VersionCatalogo(numero, CREADO)));
    }

    private static CambiosDeVersion cambiosCompletos(long version) {
        return new CambiosDeVersion(version, Set.of(107L), Set.of(204L), Set.of(308L));
    }
}
