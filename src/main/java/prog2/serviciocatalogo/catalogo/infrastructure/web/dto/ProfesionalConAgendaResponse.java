package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import java.util.List;
import prog2.serviciocatalogo.catalogo.domain.model.AgendaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;

public record ProfesionalConAgendaResponse(
        Long id,
        Long categoriaId,
        String nombre,
        String apellido,
        boolean habilitado,
        List<HorarioResponse> horarios,
        long versionCatalogo) {

    public static ProfesionalConAgendaResponse desde(AgendaProfesional agenda) {
        Profesional profesional = agenda.profesional();
        return new ProfesionalConAgendaResponse(
                profesional.id(),
                profesional.categoriaId(),
                profesional.nombre(),
                profesional.apellido(),
                profesional.habilitado(),
                agenda.horarios().stream().map(HorarioResponse::desde).toList(),
                agenda.versionCatalogo());
    }
}
