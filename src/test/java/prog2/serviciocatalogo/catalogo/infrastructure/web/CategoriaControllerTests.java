package prog2.serviciocatalogo.catalogo.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import prog2.serviciocatalogo.catalogo.domain.exception.CategoriaNoEncontradaException;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarCategoriasUseCase;

// Prueba la lógica del controlador; la seguridad tiene sus propias pruebas.
@WebMvcTest(controllers = CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");
    private static final CategoriaProfesional CLINICA =
            new CategoriaProfesional(1L, "Clinica medica", "Atencion general", true, CREADO, CREADO);

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ConsultarCategoriasUseCase consultarCategorias;

    @Test
    void listaLasCategoriasPaginadas() {
        when(consultarCategorias.listar(new PedidoPagina(0, 20))).thenReturn(new Pagina<>(List.of(CLINICA), 0, 20, 1));

        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias").exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).bodyJson().isEqualTo("""
                {"content": [{"id": 1, "nombre": "Clinica medica", "descripcion": "Atencion general", "habilitada": true}],
                 "page": {"size": 20, "number": 0, "totalElements": 1, "totalPages": 1}}
                """);
    }

    @Test
    void devuelveUnaCategoria() {
        when(consultarCategorias.obtener(1)).thenReturn(CLINICA);

        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias/1").exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).bodyJson().extractingPath("$.nombre").isEqualTo("Clinica medica");
    }

    @Test
    void unaCategoriaInexistenteDevuelve404() {
        when(consultarCategorias.obtener(9)).thenThrow(new CategoriaNoEncontradaException(9));

        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias/9").exchange();

        assertThat(resultado).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo("urn:catalogo:error:categoria-no-encontrada");
    }

    @Test
    void unaPaginaNegativaDevuelve400() {
        assertThat(mvc.get().uri("/api/v1/categorias?page=-1").exchange()).hasStatus(HttpStatus.BAD_REQUEST);
    }
}
