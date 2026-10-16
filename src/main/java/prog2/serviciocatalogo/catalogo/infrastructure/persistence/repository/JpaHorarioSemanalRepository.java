package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.HorarioSemanalEntity;

public interface JpaHorarioSemanalRepository extends JpaRepository<HorarioSemanalEntity, Long> {
}
