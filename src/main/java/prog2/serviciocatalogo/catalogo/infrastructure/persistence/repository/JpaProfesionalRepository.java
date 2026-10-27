package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;

public interface JpaProfesionalRepository extends JpaRepository<ProfesionalEntity, Long>,
        JpaSpecificationExecutor<ProfesionalEntity> {
}
