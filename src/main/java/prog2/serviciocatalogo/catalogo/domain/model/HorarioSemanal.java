package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;
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

    // Orden natural de una agenda: por día de la semana y, dentro del día, por hora de inicio.
    public static final Comparator<HorarioSemanal> POR_DIA_Y_HORA =
            Comparator.comparing(HorarioSemanal::diaSemana).thenComparing(HorarioSemanal::horaInicio);

    public HorarioSemanal {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(profesionalId, "profesionalId");
        Objects.requireNonNull(diaSemana, "diaSemana");
    }

    // Turnos completos que entran en la franja; si los datos no permiten calcularlo, no entra ninguno.
    public int cantidadTurnos() {
        if (horaInicio == null || horaFin == null || duracionTurnoMinutos <= 0 || !horaInicio.isBefore(horaFin)) {
            return 0;
        }
        return (int) (Duration.between(horaInicio, horaFin).toMinutes() / duracionTurnoMinutos);
    }
}
