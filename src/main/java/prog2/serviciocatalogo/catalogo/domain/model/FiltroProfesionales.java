package prog2.serviciocatalogo.catalogo.domain.model;

public record FiltroProfesionales(Long categoriaId, String nombre, Boolean habilitado) {

    // Un nombre vacío o con solo espacios no filtra nada.
    public FiltroProfesionales {
        nombre = nombre == null || nombre.isBlank() ? null : nombre.strip();
    }

    public boolean filtraPorCategoria() {
        return categoriaId != null;
    }

    public boolean filtraPorNombre() {
        return nombre != null;
    }

    public boolean filtraPorHabilitado() {
        return habilitado != null;
    }
}
