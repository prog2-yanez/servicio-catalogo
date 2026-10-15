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
@Table(name = "version_catalogo")
@Getter
@Setter
@NoArgsConstructor
public class VersionCatalogoEntity {

    public static final short ID_UNICO = 1;

    @Id
    private Short id;

    @Column(nullable = false)
    private long version;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}
