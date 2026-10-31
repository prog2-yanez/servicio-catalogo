package prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.EventoProcesadoEntity;

public interface JpaEventoProcesadoRepository extends JpaRepository<EventoProcesadoEntity, String> {

    // Si dos entregas del mismo evento llegan a la vez, solo una inserta; la otra no falla.
    @Modifying
    @Query(value = """
            INSERT INTO evento_procesado (event_id, tipo, procesado_en)
            VALUES (:eventId, :tipo, :procesadoEn)
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int insertarSiNoExiste(String eventId, String tipo, Instant procesadoEn);
}
