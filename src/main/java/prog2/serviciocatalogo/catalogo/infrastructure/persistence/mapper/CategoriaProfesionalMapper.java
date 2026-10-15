package prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.CategoriaProfesionalEntity;

@Component
public class CategoriaProfesionalMapper {

    public CategoriaProfesionalEntity aEntidad(CategoriaProfesional categoria) {
        CategoriaProfesionalEntity entidad = new CategoriaProfesionalEntity();
        entidad.setId(categoria.id());
        entidad.setNombre(categoria.nombre());
        entidad.setDescripcion(categoria.descripcion());
        entidad.setHabilitado(categoria.habilitado());
        entidad.setCreadoEn(categoria.creadoEn());
        entidad.setActualizadoEn(categoria.actualizadoEn());
        return entidad;
    }

    public CategoriaProfesional aDominio(CategoriaProfesionalEntity entidad) {
        return new CategoriaProfesional(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.isHabilitado(),
                entidad.getCreadoEn(),
                entidad.getActualizadoEn());
    }
}
