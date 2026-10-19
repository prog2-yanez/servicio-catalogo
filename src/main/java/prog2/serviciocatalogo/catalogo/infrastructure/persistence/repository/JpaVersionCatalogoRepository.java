package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;

public interface JpaVersionCatalogoRepository extends JpaRepository<VersionCatalogoEntity, Short> {
}
