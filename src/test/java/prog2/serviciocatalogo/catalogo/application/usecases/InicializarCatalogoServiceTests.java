package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ReconstruirCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;

class InicializarCatalogoServiceTests {

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final ReconstruirCatalogoUseCase reconstruirCatalogo = mock(ReconstruirCatalogoUseCase.class);
    private final InicializarCatalogoService servicio =
            new InicializarCatalogoService(catalogoRepository, reconstruirCatalogo);

    @Test
    void conLaBaseVaciaReconstruyeConElSnapshot() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());

        servicio.inicializarSiEstaVacio();

        verify(reconstruirCatalogo).reconstruir();
    }

    @Test
    void conDatosNoVuelveAPedirElSnapshot() {
        when(catalogoRepository.obtenerVersion())
                .thenReturn(Optional.of(new VersionCatalogo(3, Instant.parse("2026-09-10T13:40:00Z"))));

        servicio.inicializarSiEstaVacio();

        verifyNoInteractions(reconstruirCatalogo);
    }

    @Test
    void siLaCatedraFallaElServicioArrancaIgual() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        doThrow(new CatedraNoDisponibleException("HTTP 503", null)).when(reconstruirCatalogo).reconstruir();

        assertThatCode(servicio::inicializarSiEstaVacio).doesNotThrowAnyException();
    }

    @Test
    void siFallaOtraCosaPropagaLaExcepcion() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        doThrow(new IllegalStateException("la base no responde")).when(reconstruirCatalogo).reconstruir();

        assertThatThrownBy(servicio::inicializarSiEstaVacio).isInstanceOf(IllegalStateException.class);
    }
}
