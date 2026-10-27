package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;

public record CategoriaResponse(Long id, String nombre, String descripcion, boolean habilitada) {

    public static CategoriaResponse desde(CategoriaProfesional categoria) {
        return new CategoriaResponse(categoria.id(), categoria.nombre(), categoria.descripcion(), categoria.habilitado());
    }
}
