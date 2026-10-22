package prog2.serviciocatalogo.catalogo.domain.model;

import java.util.List;
import java.util.Objects;

// Lo que Turnos necesita para validar un turno: el profesional, cuándo atiende y con qué versión del catálogo.
public record AgendaProfesional(Profesional profesional, List<HorarioSemanal> horarios, long versionCatalogo) {

    public AgendaProfesional {
        Objects.requireNonNull(profesional, "profesional");
        horarios = horarios.stream()
                .filter(HorarioSemanal::habilitado)
                .sorted(HorarioSemanal.POR_DIA_Y_HORA)
                .toList();
    }
}
