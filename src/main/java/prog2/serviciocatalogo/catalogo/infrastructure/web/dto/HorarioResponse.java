package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;

public record HorarioResponse(
        Long id,
        DayOfWeek diaSemana,
        @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @JsonFormat(pattern = "HH:mm") LocalTime horaFin,
        int duracionTurnoMinutos) {

    public static HorarioResponse desde(HorarioSemanal horario) {
        return new HorarioResponse(
                horario.id(),
                horario.diaSemana(),
                horario.horaInicio(),
                horario.horaFin(),
                horario.duracionTurnoMinutos());
    }
}
