package prog2.serviciocatalogo.shared.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.constraints.Max;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.exception.CategoriaNoEncontradaException;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalDeshabilitadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalNoEncontradoException;

@WebMvcTest
@Import(ManejadorErroresApiTests.ControladorDePrueba.class)
class ManejadorErroresApiTests {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void profesionalInexistenteDevuelve404() {
        MvcTestResult resultado = mvc.get().uri("/prueba/profesionales/99").exchange();

        verificarProblema(resultado, HttpStatus.NOT_FOUND, "profesional-no-encontrado", "/prueba/profesionales/99");
        assertThat(resultado).bodyJson().extractingPath("$.detail")
                .isEqualTo("No existe un profesional con id 99");
    }

    @Test
    void categoriaInexistenteDevuelve404ConSuPropioTipo() {
        MvcTestResult resultado = mvc.get().uri("/prueba/categorias/7").exchange();

        verificarProblema(resultado, HttpStatus.NOT_FOUND, "categoria-no-encontrada", "/prueba/categorias/7");
    }

    @Test
    void profesionalDeshabilitadoDevuelve409() {
        MvcTestResult resultado = mvc.get().uri("/prueba/deshabilitado").exchange();

        verificarProblema(resultado, HttpStatus.CONFLICT, "profesional-deshabilitado", "/prueba/deshabilitado");
    }

    @Test
    void catalogoSinDatosDevuelve503() {
        MvcTestResult resultado = mvc.get().uri("/prueba/sin-datos").exchange();

        verificarProblema(resultado, HttpStatus.SERVICE_UNAVAILABLE, "catalogo-sin-datos", "/prueba/sin-datos");
    }

    @Test
    void parametroConTipoIncorrectoDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/prueba/numero?valor=abc").exchange();

        verificarProblema(resultado, HttpStatus.BAD_REQUEST, "parametro-invalido", "/prueba/numero");
    }

    @Test
    void parametroFueraDeRangoDevuelve400() {
        MvcTestResult resultado = mvc.get().uri("/prueba/pagina?size=500").exchange();

        verificarProblema(resultado, HttpStatus.BAD_REQUEST, "parametro-invalido", "/prueba/pagina");
    }

    @Test
    void rutaInexistenteDevuelve404() {
        MvcTestResult resultado = mvc.get().uri("/no-existe").exchange();

        verificarProblema(resultado, HttpStatus.NOT_FOUND, "ruta-no-encontrada", "/no-existe");
    }

    @Test
    void errorInesperadoDevuelve500SinDetallesInternos() {
        MvcTestResult resultado = mvc.get().uri("/prueba/falla").exchange();

        verificarProblema(resultado, HttpStatus.INTERNAL_SERVER_ERROR, "error-interno", "/prueba/falla");
        assertThat(resultado).bodyText().doesNotContain("select", "detalle interno");
    }

    private static void verificarProblema(
            MvcTestResult resultado, HttpStatus estado, String codigo, String instancia) {
        assertThat(resultado).hasStatus(estado);
        assertThat(resultado).hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo(ManejadorErroresApi.PREFIJO_TIPO + codigo);
        assertThat(resultado).bodyJson().extractingPath("$.status").isEqualTo(estado.value());
        assertThat(resultado).bodyJson().extractingPath("$.instance").isEqualTo(instancia);
        assertThat(resultado).bodyJson().extractingPath("$.title").isNotNull();
        assertThat(resultado).bodyJson().extractingPath("$.detail").isNotNull();
    }

    // Controlador que existe solo en las pruebas: tira cada error para verificar cómo se traduce.
    @RestController
    static class ControladorDePrueba {

        @GetMapping("/prueba/profesionales/{id}")
        String profesional(@PathVariable long id) {
            throw new ProfesionalNoEncontradoException(id);
        }

        @GetMapping("/prueba/categorias/{id}")
        String categoria(@PathVariable long id) {
            throw new CategoriaNoEncontradaException(id);
        }

        @GetMapping("/prueba/deshabilitado")
        String deshabilitado() {
            throw new ProfesionalDeshabilitadoException(15);
        }

        @GetMapping("/prueba/sin-datos")
        String sinDatos() {
            throw new CatalogoSinDatosException();
        }

        @GetMapping("/prueba/numero")
        String numero(@RequestParam int valor) {
            return String.valueOf(valor);
        }

        @GetMapping("/prueba/pagina")
        String pagina(@RequestParam @Max(100) int size) {
            return String.valueOf(size);
        }

        @GetMapping("/prueba/falla")
        String falla() {
            throw new IllegalStateException("detalle interno: select * from profesional");
        }
    }
}
