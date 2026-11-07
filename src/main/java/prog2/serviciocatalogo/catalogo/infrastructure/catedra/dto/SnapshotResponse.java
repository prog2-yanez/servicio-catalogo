package prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SnapshotResponse(
        long snapshotVersion,
        Instant generatedAt,
        List<CategoryResponse> professionalCategories,
        List<ProfessionalResponse> professionals,
        List<WeeklyScheduleResponse> weeklySchedules) {

    public SnapshotCatalogo aDominio() {
        return new SnapshotCatalogo(
                snapshotVersion,
                generatedAt,
                listaSegura(professionalCategories).stream().map(CategoryResponse::aDominio).toList(),
                listaSegura(professionals).stream().map(ProfessionalResponse::aDominio).toList(),
                listaSegura(weeklySchedules).stream().map(WeeklyScheduleResponse::aDominio).toList());
    }

    private static <T> List<T> listaSegura(List<T> lista) {
        return lista == null ? List.of() : lista;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CategoryResponse(
            Long id, String name, String description, boolean enabled,
            Instant createdAt, Instant updatedAt) {

        public CategoriaProfesional aDominio() {
            return new CategoriaProfesional(id, name, description, enabled, createdAt, updatedAt);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProfessionalResponse(
            Long id, Long categoryId, String firstName, String lastName, boolean enabled,
            Instant createdAt, Instant updatedAt) {

        public Profesional aDominio() {
            return new Profesional(id, categoryId, firstName, lastName, enabled, createdAt, updatedAt);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeeklyScheduleResponse(
            Long id, Long professionalId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
            int slotDurationMinutes, boolean enabled, Instant createdAt, Instant updatedAt) {

        public HorarioSemanal aDominio() {
            return new HorarioSemanal(id, professionalId, dayOfWeek, startTime, endTime,
                    slotDurationMinutes, enabled, createdAt, updatedAt);
        }
    }
}
