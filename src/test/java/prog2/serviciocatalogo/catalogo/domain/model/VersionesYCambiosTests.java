package prog2.serviciocatalogo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VersionesYCambiosTests {

    @Test
    void hayNovedadesSoloSiLaCatedraVaAdelante() {
        EstadoVersiones estado = new EstadoVersiones(7, 4);

        assertThat(estado.hayNovedadesPara(5)).isTrue();
        assertThat(estado.hayNovedadesPara(7)).isFalse();
    }

    // Ejemplo del contrato: local 3, más antigua 4, actual 7 → no se puede seguir de a una versión.
    @Test
    void siLaCopiaQuedoMasViejaQueLoQueConservaLaCatedraNoSeActualizaDeAUna() {
        EstadoVersiones estado = new EstadoVersiones(7, 4);

        assertThat(estado.permiteActualizarDesde(3)).isFalse();
        assertThat(estado.permiteActualizarDesde(4)).isTrue();
        assertThat(estado.permiteActualizarDesde(6)).isTrue();
    }

    @Test
    void losCambiosCuentanTodasLasEntidadesYNoSePuedenModificarDesdeAfuera() {
        Set<Long> profesionales = new HashSet<>(Set.of(204L, 205L));
        CambiosDeVersion cambios = new CambiosDeVersion(5, Set.of(107L), profesionales, Set.of(308L));

        profesionales.add(999L);

        assertThat(cambios.cantidad()).isEqualTo(4);
        assertThat(cambios.idsProfesionales()).containsExactlyInAnyOrder(204L, 205L);
    }
}
