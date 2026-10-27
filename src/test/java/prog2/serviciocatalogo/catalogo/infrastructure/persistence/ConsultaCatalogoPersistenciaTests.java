package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.CategoriaProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.HorarioSemanalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.ProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
        CategoriaProfesionalMapper.class,
        ProfesionalMapper.class,
        HorarioSemanalMapper.class,
        ConsultaProfesionalesPersistenceAdapter.class,
        ConsultaCategoriasPersistenceAdapter.class
})
class ConsultaCatalogoPersistenciaTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");
    private static final PedidoPagina PRIMERA_PAGINA = new PedidoPagina(0, 20);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaCategoriaProfesionalRepository categorias;

    @Autowired
    private JpaProfesionalRepository profesionales;

    @Autowired
    private JpaHorarioSemanalRepository horarios;

    @Autowired
    private HorarioSemanalMapper horarioMapper;

    @Autowired
    private CategoriaProfesionalMapper categoriaMapper;

    @Autowired
    private ProfesionalMapper profesionalMapper;

    @Autowired
    private ConsultaProfesionalesPersistenceAdapter consultaProfesionales;

    @Autowired
    private ConsultaCategoriasPersistenceAdapter consultaCategorias;

    @BeforeEach
    void cargarDatos() {
        categorias.saveAllAndFlush(List.of(
                categoriaMapper.aEntidad(categoria(1L, "Pediatria")),
                categoriaMapper.aEntidad(categoria(2L, "Clinica medica"))));
        profesionales.saveAllAndFlush(List.of(
                profesionalMapper.aEntidad(profesional(10L, 1L, "Ana", "Gomez")),
                profesionalMapper.aEntidad(profesional(11L, 1L, "Luis", "Perez")),
                profesionalMapper.aEntidad(profesional(12L, 2L, "Ana", "Diaz")),
                profesionalMapper.aEntidad(profesional(13L, 2L, "Marta", "Ana_lia"))));
    }

    @Test
    void sinFiltrosDevuelveTodosOrdenadosPorApellido() {
        assertThat(nombres(buscar(null, null))).containsExactly("Marta Ana_lia", "Ana Diaz", "Ana Gomez", "Luis Perez");
    }

    @Test
    void filtraPorCategoria() {
        assertThat(nombres(buscar(1L, null))).containsExactly("Ana Gomez", "Luis Perez");
    }

    @Test
    void filtraPorNombreOApellidoSinDistinguirMayusculas() {
        assertThat(nombres(buscar(null, "aNA"))).containsExactly("Marta Ana_lia", "Ana Diaz", "Ana Gomez");
    }

    @Test
    void combinaCategoriaYNombre() {
        assertThat(nombres(buscar(2L, "ana"))).containsExactly("Marta Ana_lia", "Ana Diaz");
    }

    @Test
    void losComodinesDelUsuarioSeBuscanComoTexto() {
        assertThat(nombres(buscar(null, "a_l"))).containsExactly("Marta Ana_lia");
        assertThat(buscar(null, "%").contenido()).isEmpty();
    }

    @Test
    void unaCategoriaInexistenteDevuelveUnaListaVacia() {
        assertThat(buscar(99L, null).contenido()).isEmpty();
    }

    @Test
    void filtraPorEstadoHabilitado() {
        guardarDeshabilitada();

        assertThat(nombres(buscar(null, null, true)))
                .containsExactly("Marta Ana_lia", "Ana Diaz", "Ana Gomez", "Luis Perez");
        assertThat(nombres(buscar(null, null, false))).containsExactly("Ana Rios");
    }

    @Test
    void combinaHabilitadoConCategoriaYNombre() {
        guardarDeshabilitada();

        assertThat(nombres(buscar(1L, "ana", true))).containsExactly("Ana Gomez");
        assertThat(nombres(buscar(1L, "ana", false))).containsExactly("Ana Rios");
        assertThat(nombres(buscar(1L, "ana", null))).containsExactly("Ana Gomez", "Ana Rios");
    }

    @Test
    void sinDatosLasConsultasDevuelvenListasVaciasYNingunId() {
        profesionales.deleteAllInBatch();
        categorias.deleteAllInBatch();
        // El borrado en lote no pasa por el contexto de persistencia: se limpia para no leer entidades viejas.
        entityManager.clear();

        assertThat(buscar(null, null).contenido()).isEmpty();
        assertThat(consultaCategorias.listar(PRIMERA_PAGINA).contenido()).isEmpty();
        assertThat(consultaProfesionales.buscarPorId(10)).isEmpty();
        assertThat(consultaCategorias.buscarPorId(1)).isEmpty();
    }

    @Test
    void devuelveLaPaginaPedidaConElTotal() {
        Pagina<Profesional> pagina = consultaProfesionales.buscar(
                new FiltroProfesionales(null, null, null), new PedidoPagina(1, 3));

        assertThat(nombres(pagina)).containsExactly("Luis Perez");
        assertThat(pagina.totalElementos()).isEqualTo(4);
        assertThat(pagina.totalPaginas()).isEqualTo(2);
    }

    @Test
    void listaLasCategoriasOrdenadasPorNombreYBuscaPorId() {
        assertThat(consultaCategorias.listar(PRIMERA_PAGINA).contenido())
                .extracting(CategoriaProfesional::nombre)
                .containsExactly("Clinica medica", "Pediatria");
        assertThat(consultaCategorias.buscarPorId(1L)).map(CategoriaProfesional::nombre).contains("Pediatria");
        assertThat(consultaCategorias.buscarPorId(99L)).isEmpty();
    }

    @Test
    void buscaUnProfesionalPorId() {
        assertThat(consultaProfesionales.buscarPorId(10L)).map(Profesional::nombreCompleto).contains("Ana Gomez");
        assertThat(consultaProfesionales.buscarPorId(99L)).isEmpty();
    }

    @Test
    void devuelveSoloLosHorariosHabilitadosDelProfesional() {
        horarios.saveAllAndFlush(List.of(
                horarioMapper.aEntidad(horario(21L, 10L, DayOfWeek.MONDAY, true)),
                horarioMapper.aEntidad(horario(22L, 10L, DayOfWeek.TUESDAY, false)),
                horarioMapper.aEntidad(horario(23L, 11L, DayOfWeek.MONDAY, true))));

        assertThat(consultaProfesionales.horariosHabilitados(10L)).extracting(HorarioSemanal::id).containsExactly(21L);
    }

    private Pagina<Profesional> buscar(Long categoriaId, String nombre) {
        return buscar(categoriaId, nombre, null);
    }

    private Pagina<Profesional> buscar(Long categoriaId, String nombre, Boolean habilitado) {
        return consultaProfesionales.buscar(new FiltroProfesionales(categoriaId, nombre, habilitado), PRIMERA_PAGINA);
    }

    private void guardarDeshabilitada() {
        profesionales.saveAndFlush(profesionalMapper.aEntidad(
                new Profesional(14L, 1L, "Ana", "Rios", false, CREADO, CREADO)));
    }

    private static List<String> nombres(Pagina<Profesional> pagina) {
        return pagina.contenido().stream().map(Profesional::nombreCompleto).toList();
    }

    private static CategoriaProfesional categoria(Long id, String nombre) {
        return new CategoriaProfesional(id, nombre, "Descripcion", true, CREADO, CREADO);
    }

    private static Profesional profesional(Long id, Long categoriaId, String nombre, String apellido) {
        return new Profesional(id, categoriaId, nombre, apellido, true, CREADO, CREADO);
    }

    private static HorarioSemanal horario(Long id, Long profesionalId, DayOfWeek dia, boolean habilitado) {
        return new HorarioSemanal(id, profesionalId, dia, LocalTime.of(9, 0), LocalTime.of(12, 0), 30,
                habilitado, CREADO, CREADO);
    }
}
