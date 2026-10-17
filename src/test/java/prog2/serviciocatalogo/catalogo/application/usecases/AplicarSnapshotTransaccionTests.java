package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.CategoriaProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AplicarSnapshotTransaccionTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");

    @Autowired
    private AplicarSnapshotUseCase aplicarSnapshot;

    @Autowired
    private JpaCategoriaProfesionalRepository categorias;

    @Autowired
    private JpaProfesionalRepository profesionales;

    @Autowired
    private JpaHorarioSemanalRepository horarios;

    @Autowired
    private JpaVersionCatalogoRepository versiones;

    @BeforeEach
    void limpiarBase() {
        horarios.deleteAllInBatch();
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        versiones.deleteAllInBatch();
    }

    @Test
    void guardaLasTresColeccionesYLaVersionDelSnapshot() {
        aplicarSnapshot.aplicar(snapshot(5, "Clinica medica", 15L));

        assertThat(categorias.count()).isEqualTo(1);
        assertThat(profesionales.count()).isEqualTo(1);
        assertThat(horarios.count()).isEqualTo(1);
        assertThat(versionLocal()).isEqualTo(5);
    }

    @Test
    void repetirElMismoSnapshotNoDuplicaDatos() {
        aplicarSnapshot.aplicar(snapshot(5, "Clinica medica", 15L));
        aplicarSnapshot.aplicar(snapshot(5, "Clinica medica", 15L));

        assertThat(categorias.count()).isEqualTo(1);
        assertThat(profesionales.count()).isEqualTo(1);
        assertThat(horarios.count()).isEqualTo(1);
        assertThat(versiones.count()).isEqualTo(1);
    }

    @Test
    void unSnapshotNuevoReemplazaLaCopiaAnterior() {
        aplicarSnapshot.aplicar(snapshot(5, "Clinica medica", 15L));
        aplicarSnapshot.aplicar(new SnapshotCatalogo(6, CREADO,
                List.of(new CategoriaProfesional(2L, "Pediatria", "Atencion infantil", true, CREADO, CREADO)),
                List.of(), List.of()));

        assertThat(categorias.findAll()).extracting(CategoriaProfesionalEntity::getNombre)
                .containsExactly("Pediatria");
        assertThat(profesionales.count()).isZero();
        assertThat(horarios.count()).isZero();
        assertThat(versionLocal()).isEqualTo(6);
    }

    @Test
    void siFallaAMitadConservaLaCopiaYLaVersionAnteriores() {
        aplicarSnapshot.aplicar(snapshot(5, "Clinica medica", 15L));

        // El horario apunta a un profesional que no viene en el snapshot, así que falla la clave foránea.
        assertThatThrownBy(() -> aplicarSnapshot.aplicar(snapshot(6, "Pediatria", 99L)))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(categorias.findAll()).extracting(CategoriaProfesionalEntity::getNombre)
                .containsExactly("Clinica medica");
        assertThat(profesionales.count()).isEqualTo(1);
        assertThat(horarios.count()).isEqualTo(1);
        assertThat(versionLocal()).isEqualTo(5);
    }

    private long versionLocal() {
        return versiones.findById(VersionCatalogoEntity.ID_UNICO).orElseThrow().getVersion();
    }

    private static SnapshotCatalogo snapshot(long version, String nombreCategoria, long profesionalDelHorario) {
        return new SnapshotCatalogo(version, CREADO,
                List.of(new CategoriaProfesional(1L, nombreCategoria, "Atencion general", true, CREADO, CREADO)),
                List.of(new Profesional(15L, 1L, "Ana", "Gomez", true, CREADO, CREADO)),
                List.of(new HorarioSemanal(21L, profesionalDelHorario, DayOfWeek.MONDAY,
                        LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true, CREADO, CREADO)));
    }
}
