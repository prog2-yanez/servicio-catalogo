package prog2.serviciocatalogo.catalogo.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalDeshabilitadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalNoEncontradoException;
import prog2.serviciocatalogo.catalogo.domain.model.AgendaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarProfesionalUseCase;

// Prueba la lógica del controlador; la seguridad tiene sus propias pruebas.
@WebMvcTest(controllers = ProfesionalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfesionalControllerTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BuscarProfesionalesUseCase buscarProfesionales;

    @MockitoBean
    private ConsultarProfesionalUseCase consultarProfesional;

    @Test
    void pasaLosFiltrosYDevuelveLaPaginaConElFormatoDelContrato() {
        Profesional ana = new Profesional(10L, 1L, "Ana", "Gomez", true, CREADO, CREADO);
        when(buscarProfesionales.buscar(new FiltroProfesionales(1L, "ana", null), new PedidoPagina(2, 5)))
                .thenReturn(new Pagina<>(List.of(ana), 2, 5, 11));

        MvcTestResult resultado = mvc.get()
                .uri("/api/v1/profesionales?categoriaId=1&nombre=ana&page=2&size=5").exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).bodyJson().isEqualTo("""
                {"content": [{"id": 10, "categoriaId": 1, "nombre": "Ana", "apellido": "Gomez", "habilitado": true}],
                 "page": {"size": 5, "number": 2, "totalElements": 11, "totalPages": 3}}
                """);
    }

    @Test
    void sinParametrosUsaLaPrimeraPaginaDeVeinte() {
        when(buscarProfesionales.buscar(any(), any())).thenReturn(new Pagina<>(List.of(), 0, 20, 0));

        assertThat(mvc.get().uri("/api/v1/profesionales").exchange()).hasStatusOk();

        verify(buscarProfesionales).buscar(new FiltroProfesionales(null, null, null), new PedidoPagina(0, 20));
    }

    @Test
    void pasaElFiltroDeHabilitado() {
        when(buscarProfesionales.buscar(any(), any())).thenReturn(new Pagina<>(List.of(), 0, 20, 0));

        assertThat(mvc.get().uri("/api/v1/profesionales?habilitado=false").exchange()).hasStatusOk();

        verify(buscarProfesionales).buscar(new FiltroProfesionales(null, null, false), new PedidoPagina(0, 20));
    }

    @Test
    void unHabilitadoQueNoEsBooleanoDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?habilitado=abc").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.type").isEqualTo("urn:catalogo:error:parametro-invalido");
        assertThat(resultado).bodyJson().extractingPath("$.title").isEqualTo("Parámetro inválido");
        assertThat(resultado).bodyJson().extractingPath("$.detail")
                .isEqualTo("El parámetro 'habilitado' tiene que ser true o false");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void unaCategoriaQueNoEsNumeroDevuelve400NombrandoElParametro() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?categoriaId=uno").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail")
                .isEqualTo("El parámetro 'categoriaId' tiene que ser un número entero");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void unaCategoriaQueNoEsPositivaDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?categoriaId=0").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail").asString()
                .contains("categoriaId", "debe ser mayor a 0");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void unNombreDeMasDe100CaracteresDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?nombre=" + "a".repeat(101)).exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail").asString()
                .contains("nombre", "100 caracteres o menos");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void unNombreDe100CaracteresSeAcepta() {
        when(buscarProfesionales.buscar(any(), any())).thenReturn(new Pagina<>(List.of(), 0, 20, 0));

        assertThat(mvc.get().uri("/api/v1/profesionales?nombre=" + "a".repeat(100)).exchange()).hasStatusOk();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-3"})
    void unIdQueNoEsPositivoDevuelve400(String id) {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales/" + id).exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail").asString().contains("id", "debe ser mayor a 0");
        verifyNoInteractions(consultarProfesional);
    }

    @Test
    void unIdQueNoEsNumeroDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales/abc").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail")
                .isEqualTo("El parámetro 'id' tiene que ser un número entero");
        verifyNoInteractions(consultarProfesional);
    }

    @Test
    void unTamanioFueraDeRangoDevuelve400ConElParametroQueFallo() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?size=500").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail").asString().contains("size", "100");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void devuelveElProfesionalConSuAgendaConElFormatoDelContrato() {
        Profesional ana = new Profesional(15L, 1L, "Ana", "Gomez", true, CREADO, CREADO);
        HorarioSemanal lunes = new HorarioSemanal(21L, 15L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true, CREADO, CREADO);
        when(consultarProfesional.obtenerConAgenda(15)).thenReturn(new AgendaProfesional(ana, List.of(lunes), 7));

        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales/15").exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).bodyJson().isEqualTo("""
                {"id": 15, "categoriaId": 1, "nombre": "Ana", "apellido": "Gomez", "habilitado": true,
                 "horarios": [{"id": 21, "diaSemana": "MONDAY", "horaInicio": "09:00", "horaFin": "12:00",
                               "duracionTurnoMinutos": 30}],
                 "versionCatalogo": 7}
                """);
    }

    @Test
    void unProfesionalInexistenteDevuelve404() {
        when(consultarProfesional.obtenerConAgenda(99)).thenThrow(new ProfesionalNoEncontradoException(99));

        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales/99").exchange();

        assertThat(resultado).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo("urn:catalogo:error:profesional-no-encontrado");
    }

    @Test
    void unProfesionalDeshabilitadoDevuelve409() {
        when(consultarProfesional.obtenerConAgenda(16)).thenThrow(new ProfesionalDeshabilitadoException(16));

        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales/16").exchange();

        assertThat(resultado).hasStatus(HttpStatus.CONFLICT);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo("urn:catalogo:error:profesional-deshabilitado");
    }
}
