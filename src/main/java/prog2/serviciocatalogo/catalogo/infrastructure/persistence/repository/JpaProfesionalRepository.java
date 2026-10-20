package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;

public interface JpaProfesionalRepository extends JpaRepository<ProfesionalEntity, Long> {
}
