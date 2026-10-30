package prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Set;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;

// Contenido de catedra:sync:changes:{version}.
@JsonIgnoreProperties(ignoreUnknown = true)
public record CambiosResponse(long version, Cambios changes) {

    public CambiosDeVersion aDominio() {
        Cambios cambios = changes == null ? new Cambios(null, null, null) : changes;
        return new CambiosDeVersion(version, enConjunto(cambios.professionalCategories()),
                enConjunto(cambios.professionals()), enConjunto(cambios.weeklySchedules()));
    }

    private static Set<Long> enConjunto(List<Long> ids) {
        return ids == null ? Set.of() : Set.copyOf(ids);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cambios(List<Long> professionalCategories, List<Long> professionals, List<Long> weeklySchedules) {
    }
}
