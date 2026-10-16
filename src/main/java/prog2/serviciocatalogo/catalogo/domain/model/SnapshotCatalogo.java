package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.Instant;
import java.util.List;

public record SnapshotCatalogo(
        long version,
        Instant generadoEn,
        List<CategoriaProfesional> categorias,
        List<Profesional> profesionales,
        List<HorarioSemanal> horarios) {

    public SnapshotCatalogo {
        categorias = List.copyOf(categorias);
        profesionales = List.copyOf(profesionales);
        horarios = List.copyOf(horarios);
    }
}
