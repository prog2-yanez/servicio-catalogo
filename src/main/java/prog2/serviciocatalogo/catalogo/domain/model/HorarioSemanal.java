package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

public record HorarioSemanal(
        Long id,
        Long profesionalId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        int duracionTurnoMinutos,
        boolean habilitado,
        Instant creadoEn,
        Instant actualizadoEn) {

    public HorarioSemanal {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(profesionalId, "profesionalId");
        Objects.requireNonNull(diaSemana, "diaSemana");
    }
}
