package prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;

@Component
public class ProfesionalMapper {

    public ProfesionalEntity aEntidad(Profesional profesional) {
        ProfesionalEntity entidad = new ProfesionalEntity();
        entidad.setId(profesional.id());
        entidad.setCategoriaId(profesional.categoriaId());
        entidad.setNombre(profesional.nombre());
        entidad.setApellido(profesional.apellido());
        entidad.setHabilitado(profesional.habilitado());
        entidad.setCreadoEn(profesional.creadoEn());
        entidad.setActualizadoEn(profesional.actualizadoEn());
        return entidad;
    }

    public Profesional aDominio(ProfesionalEntity entidad) {
        return new Profesional(
                entidad.getId(),
                entidad.getCategoriaId(),
                entidad.getNombre(),
                entidad.getApellido(),
                entidad.isHabilitado(),
                entidad.getCreadoEn(),
                entidad.getActualizadoEn());
    }
}
