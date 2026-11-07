package prog2.serviciocatalogo.catalogo.domain.model;

import java.util.Set;

// Qué entidades cambiaron en una versión. Solo trae los ids: los datos se leen aparte, uno por uno.
public record CambiosDeVersion(
        long version, Set<Long> idsCategorias, Set<Long> idsProfesionales, Set<Long> idsHorarios) {

    public CambiosDeVersion {
        idsCategorias = Set.copyOf(idsCategorias);
        idsProfesionales = Set.copyOf(idsProfesionales);
        idsHorarios = Set.copyOf(idsHorarios);
    }

    public int cantidad() {
        return idsCategorias.size() + idsProfesionales.size() + idsHorarios.size();
    }
}
