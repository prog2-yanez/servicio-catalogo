package prog2.serviciocatalogo.catalogo.domain.model;

import java.util.List;

// Una versión lista para aplicar: los cambios ya leídos de la cátedra, completos.
public record ActualizacionDeVersion(
        long version,
        List<CategoriaProfesional> categorias,
        List<Profesional> profesionales,
        List<HorarioSemanal> horarios) {

    public ActualizacionDeVersion {
        categorias = List.copyOf(categorias);
        profesionales = List.copyOf(profesionales);
        horarios = List.copyOf(horarios);
    }

    public int cantidad() {
        return categorias.size() + profesionales.size() + horarios.size();
    }
}
