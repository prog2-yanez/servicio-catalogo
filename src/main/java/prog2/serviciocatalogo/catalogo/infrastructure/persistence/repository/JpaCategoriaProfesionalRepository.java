package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.CategoriaProfesionalEntity;

public interface JpaCategoriaProfesionalRepository extends JpaRepository<CategoriaProfesionalEntity, Long> {
}
