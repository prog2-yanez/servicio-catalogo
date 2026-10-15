package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record CategoriaProfesional(
        Long id,
        String nombre,
        String descripcion,
        boolean habilitado,
        Instant creadoEn,
        Instant actualizadoEn) {

    public CategoriaProfesional {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nombre, "nombre");
    }
}
