package prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;

@Component
public class VersionCatalogoMapper {

    public VersionCatalogoEntity aEntidad(VersionCatalogo version) {
        VersionCatalogoEntity entidad = new VersionCatalogoEntity();
        entidad.setId(VersionCatalogoEntity.ID_UNICO);
        entidad.setVersion(version.numero());
        entidad.setActualizadoEn(version.actualizadoEn());
        return entidad;
    }

    public VersionCatalogo aDominio(VersionCatalogoEntity entidad) {
        return new VersionCatalogo(entidad.getVersion(), entidad.getActualizadoEn());
    }
}
