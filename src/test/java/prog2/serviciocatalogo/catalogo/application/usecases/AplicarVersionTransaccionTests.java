package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

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
import prog2.serviciocatalogo.catalogo.domain.model.ActualizacionDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AplicarVersionTransaccionTests {

    private static final Instant CREADO = Instant.parse("2026-09-01T03:00:00Z");

    @Autowired
    private AplicarVersionService aplicarVersion;

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

    // Copia local en la versión 4 con una categoría y un profesional habilitado.
    @BeforeEach
    void copiaLocalEnLaVersion4() {
        horarios.deleteAllInBatch();
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        versiones.deleteAllInBatch();
        aplicarSnapshot.aplicar(new SnapshotCatalogo(4, CREADO, List.of(categoria(107L)),
                List.of(profesional(204L, true)), List.of()));
    }

    @Test
    void actualizaLoExistenteAgregaLoNuevoYAvanzaLaVersion() {
        aplicarVersion.aplicar(new ActualizacionDeVersion(5, List.of(),
                List.of(profesional(204L, false), profesional(205L, true)), List.of(horario(308L, 205L))));

        assertThat(profesionales.findAll()).extracting(ProfesionalEntity::getId, ProfesionalEntity::isHabilitado)
                .containsExactlyInAnyOrder(
                        tuple(204L, false),
                        tuple(205L, true));
        assertThat(horarios.count()).isEqualTo(1);
        assertThat(categorias.count()).isEqualTo(1);
        assertThat(versionLocal()).isEqualTo(5);
    }

    @Test
    void unaBajaSeGuardaComoDeshabilitadaSinBorrarNada() {
        aplicarVersion.aplicar(new ActualizacionDeVersion(5, List.of(), List.of(profesional(204L, false)), List.of()));

        assertThat(profesionales.findById(204L)).get().extracting(ProfesionalEntity::isHabilitado).isEqualTo(false);
        assertThat(profesionales.count()).isEqualTo(1);
    }

    @Test
    void siFallaAMitadDeLaVersionNoQuedaNadaDeEllaYLaVersionNoAvanza() {
        // El horario apunta a un profesional que no existe, así que falla la clave foránea después de guardar
        // el profesional 205: toda la versión se deshace.
        ActualizacionDeVersion conError = new ActualizacionDeVersion(5, List.of(),
                List.of(profesional(205L, true)), List.of(horario(308L, 999L)));

        assertThatThrownBy(() -> aplicarVersion.aplicar(conError))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(profesionales.existsById(205L)).isFalse();
        assertThat(horarios.count()).isZero();
        assertThat(versionLocal()).isEqualTo(4);
    }

    private long versionLocal() {
        return versiones.findById(VersionCatalogoEntity.ID_UNICO).orElseThrow().getVersion();
    }

    private static CategoriaProfesional categoria(long id) {
        return new CategoriaProfesional(id, "Ginecologia", "Atencion ginecologica", true, CREADO, CREADO);
    }

    private static Profesional profesional(long id, boolean habilitado) {
        return new Profesional(id, 107L, "Laura", "Medina", habilitado, CREADO, CREADO);
    }

    private static HorarioSemanal horario(long id, long profesionalId) {
        return new HorarioSemanal(id, profesionalId, DayOfWeek.TUESDAY,
                LocalTime.of(17, 0), LocalTime.of(20, 0), 30, true, CREADO, CREADO);
    }
}
