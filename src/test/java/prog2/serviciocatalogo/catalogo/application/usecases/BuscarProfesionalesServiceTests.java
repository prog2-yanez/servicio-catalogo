package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

class BuscarProfesionalesServiceTests {

    private static final FiltroProfesionales FILTRO = new FiltroProfesionales(1L, "ana");
    private static final PedidoPagina PEDIDO = new PedidoPagina(0, 20);

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final ConsultaProfesionalesRepository consulta = mock(ConsultaProfesionalesRepository.class);
    private final BuscarProfesionalesService servicio = new BuscarProfesionalesService(catalogoRepository, consulta);

    @Test
    void conDatosDevuelveLaPaginaDeLaCopiaLocal() {
        Pagina<Profesional> pagina = new Pagina<>(List.of(), 0, 20, 0);
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.of(new VersionCatalogo(7, Instant.now())));
        when(consulta.buscar(FILTRO, PEDIDO)).thenReturn(pagina);

        assertThat(servicio.buscar(FILTRO, PEDIDO)).isSameAs(pagina);
    }

    @Test
    void sinDatosAvisaQueElCatalogoNoEstaListo() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.buscar(FILTRO, PEDIDO)).isInstanceOf(CatalogoSinDatosException.class);
        verifyNoInteractions(consulta);
    }
}
