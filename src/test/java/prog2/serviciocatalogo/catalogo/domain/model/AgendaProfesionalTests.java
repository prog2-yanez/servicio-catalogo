package prog2.serviciocatalogo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgendaProfesionalTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");
    private static final Profesional ANA = new Profesional(15L, 1L, "Ana", "Gomez", true, CREADO, CREADO);

    @Test
    void dejaSoloLosHorariosHabilitadosOrdenadosPorDiaYHora() {
        AgendaProfesional agenda = new AgendaProfesional(ANA, List.of(
                horario(1L, DayOfWeek.WEDNESDAY, 14, 18, 30, true),
                horario(2L, DayOfWeek.MONDAY, 15, 18, 30, true),
                horario(3L, DayOfWeek.TUESDAY, 9, 12, 30, false),
                horario(4L, DayOfWeek.MONDAY, 9, 12, 30, true)), 7);

        assertThat(agenda.horarios()).extracting(HorarioSemanal::id).containsExactly(4L, 2L, 1L);
        assertThat(agenda.versionCatalogo()).isEqualTo(7);
    }

    @Test
    void calculaLosTurnosCompletosQueEntranEnLaFranja() {
        assertThat(horario(1L, DayOfWeek.MONDAY, 9, 12, 30, true).cantidadTurnos()).isEqualTo(6);
        assertThat(horario(1L, DayOfWeek.MONDAY, 9, 10, 40, true).cantidadTurnos()).isEqualTo(1);
    }

    @Test
    void unaFranjaInconsistenteNoOfreceTurnos() {
        assertThat(horario(1L, DayOfWeek.MONDAY, 12, 9, 30, true).cantidadTurnos()).isZero();
        assertThat(horario(1L, DayOfWeek.MONDAY, 9, 12, 0, true).cantidadTurnos()).isZero();
    }

    private static HorarioSemanal horario(
            Long id, DayOfWeek dia, int desde, int hasta, int duracion, boolean habilitado) {
        return new HorarioSemanal(id, 15L, dia, LocalTime.of(desde, 0), LocalTime.of(hasta, 0),
                duracion, habilitado, CREADO, CREADO);
    }
}
