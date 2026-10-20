package prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria_profesional")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaProfesionalEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false)
    private boolean habilitado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}
