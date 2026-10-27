package prog2.serviciocatalogo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class ConsultaPaginadaTests {

    @Test
    void elFiltroIgnoraUnNombreVacioYLimpiaLosEspacios() {
        assertThat(new FiltroProfesionales(null, "   ", null).filtraPorNombre()).isFalse();
        assertThat(new FiltroProfesionales(null, null, null).filtraPorNombre()).isFalse();
        assertThat(new FiltroProfesionales(null, "  Ana ", null).nombre()).isEqualTo("Ana");
        assertThat(new FiltroProfesionales(3L, null, null).filtraPorCategoria()).isTrue();
    }

    @Test
    void elPedidoDePaginaRechazaValoresFueraDeRango() {
        assertThatThrownBy(() -> new PedidoPagina(-1, 20)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PedidoPagina(0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PedidoPagina(0, 101)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void laPaginaCalculaElTotalDePaginas() {
        assertThat(new Pagina<>(List.of(), 0, 20, 0).totalPaginas()).isZero();
        assertThat(new Pagina<>(List.of(), 0, 20, 20).totalPaginas()).isEqualTo(1);
        assertThat(new Pagina<>(List.of(), 0, 20, 21).totalPaginas()).isEqualTo(2);
    }

    @Test
    void convertirLaPaginaConservaLosDatosDePaginacion() {
        Pagina<Integer> convertida = new Pagina<>(List.of("a", "bb"), 1, 2, 5).map(String::length);

        assertThat(convertida).isEqualTo(new Pagina<>(List.of(1, 2), 1, 2, 5));
    }
}
