package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.AplicarSnapshotUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class InicializarCatalogoIntegracionTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");

    private final CatedraClient catedraClient = mock(CatedraClient.class);

    @Autowired
    private AplicarSnapshotUseCase aplicarSnapshot;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private RegistroSincronizacion registroSincronizacion;

    @Autowired
    private JpaCategoriaProfesionalRepository categorias;

    @Autowired
    private JpaProfesionalRepository profesionales;

    @Autowired
    private JpaHorarioSemanalRepository horarios;

    @Autowired
    private JpaVersionCatalogoRepository versiones;

    private InicializarCatalogoService inicializarCatalogo;

    // La cátedra se simula; la base y la transacción son las reales.
    @BeforeEach
    void preparar() {
        inicializarCatalogo = new InicializarCatalogoService(catalogoRepository,
                new ReconstruirCatalogoService(catedraClient, aplicarSnapshot, registroSincronizacion));
        horarios.deleteAllInBatch();
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        versiones.deleteAllInBatch();
    }

    @Test
    void conLaBaseVaciaGuardaElSnapshotYSuVersion() {
        when(catedraClient.obtenerSnapshot()).thenReturn(new SnapshotCatalogo(4, CREADO,
                List.of(new CategoriaProfesional(1L, "Clinica medica", "Atencion general", true, CREADO, CREADO)),
                List.of(), List.of()));

        inicializarCatalogo.inicializarSiEstaVacio();

        assertThat(categorias.count()).isEqualTo(1);
        assertThat(catalogoRepository.obtenerVersion())
                .hasValueSatisfying(version -> assertThat(version.numero()).isEqualTo(4));
    }

    @Test
    void siLaCatedraFallaNoInventaUnaVersion() {
        when(catedraClient.obtenerSnapshot()).thenThrow(new CatedraNoDisponibleException("HTTP 503", null));

        inicializarCatalogo.inicializarSiEstaVacio();

        assertThat(catalogoRepository.obtenerVersion()).isEmpty();
        assertThat(categorias.count()).isZero();
    }
}
