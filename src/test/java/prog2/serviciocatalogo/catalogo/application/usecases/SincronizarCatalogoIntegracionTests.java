package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CambiosCatalogoCatedra;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

// Un caso por cada motivo que obliga a reconstruir. La cátedra se simula; la base y las transacciones son reales.
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SincronizarCatalogoIntegracionTests {

    private static final Instant CREADO = Instant.parse("2026-09-01T03:00:00Z");

    // En la cátedra, el profesional 204 se dio de baja y apareció el 205.
    private static final SnapshotCatalogo SNAPSHOT_VERSION_9 = new SnapshotCatalogo(9, CREADO,
            List.of(categoria(107L)), List.of(profesional(204L, false), profesional(205L, true)),
            List.of(horario(308L, 205L)));

    private final CambiosCatalogoCatedra cambiosCatedra = mock(CambiosCatalogoCatedra.class);
    private final CatedraClient catedraClient = mock(CatedraClient.class);

    @Autowired
    private AplicarVersionService aplicarVersion;

    @Autowired
    private AplicarSnapshotUseCase aplicarSnapshot;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private RegistroSincronizacion registro;

    @Autowired
    private JpaCategoriaProfesionalRepository categorias;

    @Autowired
    private JpaProfesionalRepository profesionales;

    @Autowired
    private JpaHorarioSemanalRepository horarios;

    @Autowired
    private JpaVersionCatalogoRepository versiones;

    private SincronizarCatalogoService sincronizar;

    // Copia local en la versión 4 con una categoría y el profesional 204 habilitado.
    @BeforeEach
    void copiaLocalEnLaVersion4() {
        sincronizar = new SincronizarCatalogoService(
                new ActualizarCatalogoService(catalogoRepository, cambiosCatedra, aplicarVersion, registro),
                new ReconstruirCatalogoService(catedraClient, aplicarSnapshot, registro));
        horarios.deleteAllInBatch();
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        versiones.deleteAllInBatch();
        aplicarSnapshot.aplicar(new SnapshotCatalogo(4, CREADO, List.of(categoria(107L)),
                List.of(profesional(204L, true)), List.of()));
        when(catedraClient.obtenerSnapshot()).thenReturn(SNAPSHOT_VERSION_9);
    }

    // Ejemplo del contrato: la cátedra ya no guarda los cambios de la versión 5.
    @Test
    void siLaCopiaEsMasViejaQueLaVersionMasAntiguaReconstruye() {
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(9, 6)));

        assertThat(sincronizar.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verificarQueQuedoIgualAlSnapshot();
    }

    @Test
    void siFaltanLosCambiosDeUnaVersionReconstruye() {
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(9, 4)));
        when(cambiosCatedra.cambios(5)).thenReturn(Optional.empty());

        assertThat(sincronizar.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verificarQueQuedoIgualAlSnapshot();
    }

    @Test
    void siFaltaUnaEntidadDeLaVersionReconstruye() {
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(9, 4)));
        when(cambiosCatedra.cambios(5))
                .thenReturn(Optional.of(new CambiosDeVersion(5, Set.of(), Set.of(299L), Set.of())));
        when(cambiosCatedra.profesional(299L)).thenReturn(Optional.empty());

        assertThat(sincronizar.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verificarQueQuedoIgualAlSnapshot();
    }

    @Test
    void sinLosMetadatosDeVersionesReconstruye() {
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.empty());

        assertThat(sincronizar.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verificarQueQuedoIgualAlSnapshot();
    }

    @Test
    void sinCopiaLocalReconstruye() {
        horarios.deleteAllInBatch();
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        versiones.deleteAllInBatch();
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(9, 4)));

        assertThat(sincronizar.sincronizar()).isEqualTo(ResultadoActualizacion.RECONSTRUIDO);

        verificarQueQuedoIgualAlSnapshot();
    }

    @Test
    void siLaReconstruccionFallaQuedaLaCopiaAnteriorEntera() {
        when(cambiosCatedra.estadoVersiones()).thenReturn(Optional.of(new EstadoVersiones(9, 6)));
        // El horario apunta a un profesional que no está en el snapshot: falla la clave foránea al final.
        when(catedraClient.obtenerSnapshot()).thenReturn(new SnapshotCatalogo(9, CREADO,
                List.of(categoria(107L)), List.of(profesional(205L, true)), List.of(horario(308L, 999L))));

        assertThatThrownBy(sincronizar::sincronizar).isInstanceOf(DataIntegrityViolationException.class);

        assertThat(profesionales.findAll()).extracting(ProfesionalEntity::getId, ProfesionalEntity::isHabilitado)
                .containsExactly(tuple(204L, true));
        assertThat(horarios.count()).isZero();
        assertThat(versionLocal()).isEqualTo(4);
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.ERROR);
    }

    private void verificarQueQuedoIgualAlSnapshot() {
        assertThat(profesionales.findAll()).extracting(ProfesionalEntity::getId, ProfesionalEntity::isHabilitado)
                .containsExactlyInAnyOrder(
                        tuple(204L, false),
                        tuple(205L, true));
        assertThat(horarios.count()).isEqualTo(1);
        assertThat(categorias.count()).isEqualTo(1);
        assertThat(versionLocal()).isEqualTo(9);
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.SINCRONIZADO);
    }

    private long versionLocal() {
        return catalogoRepository.obtenerVersion().orElseThrow().numero();
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
