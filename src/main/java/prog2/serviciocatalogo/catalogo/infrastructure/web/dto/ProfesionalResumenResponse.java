package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import prog2.serviciocatalogo.catalogo.domain.model.Profesional;

public record ProfesionalResumenResponse(Long id, Long categoriaId, String nombre, String apellido, boolean habilitado) {

    public static ProfesionalResumenResponse desde(Profesional profesional) {
        return new ProfesionalResumenResponse(
                profesional.id(),
                profesional.categoriaId(),
                profesional.nombre(),
                profesional.apellido(),
                profesional.habilitado());
    }
}
