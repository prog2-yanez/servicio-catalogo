package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

class BuscarProfesionalesServiceTests {

    private static final FiltroProfesionales FILTRO = new FiltroProfesionales(1L, "ana", null);
    private static final PedidoPagina PEDIDO = new PedidoPagina(0, 20);

    private final ConsultaProfesionalesRepository consulta = mock(ConsultaProfesionalesRepository.class);
    private final BuscarProfesionalesService servicio = new BuscarProfesionalesService(consulta);

    @Test
    void devuelveLaPaginaDeLaCopiaLocal() {
        Pagina<Profesional> pagina = new Pagina<>(List.of(), 0, 20, 0);
        when(consulta.buscar(FILTRO, PEDIDO)).thenReturn(pagina);

        assertThat(servicio.buscar(FILTRO, PEDIDO)).isSameAs(pagina);
    }
}
