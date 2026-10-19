package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record Profesional(
        Long id,
        Long categoriaId,
        String nombre,
        String apellido,
        boolean habilitado,
        Instant creadoEn,
        Instant actualizadoEn) {

    public Profesional {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(categoriaId, "categoriaId");
    }

    public String nombreCompleto() {
        return nombre + " " + apellido;
    }
}
