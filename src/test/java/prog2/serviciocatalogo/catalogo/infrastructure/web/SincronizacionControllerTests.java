package prog2.serviciocatalogo.catalogo.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.model.SituacionSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarEstadoSincronizacionUseCase;

@WebMvcTest(controllers = SincronizacionController.class)
class SincronizacionControllerTests {

    private static final String RUTA = "/api/v1/sincronizacion/estado";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ConsultarEstadoSincronizacionUseCase consultarEstado;

    @Test
    void informaElEstadoYLaVersionLocal() {
        when(consultarEstado.consultar()).thenReturn(new SituacionSincronizacion(
                EstadoSincronizacion.SINCRONIZADO, 7L, Instant.parse("2026-10-19T13:40:00Z")));

        MvcTestResult resultado = mvc.get().uri(RUTA).exchange();

        assertThat(resultado).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);
        assertThat(resultado).bodyJson().isEqualTo("""
                {"estado": "SINCRONIZADO", "versionLocal": 7, "actualizadoEn": "2026-10-19T13:40:00Z"}
                """);
    }

    @Test
    void sinDatosDevuelveLaVersionEnNull() {
        when(consultarEstado.consultar())
                .thenReturn(new SituacionSincronizacion(EstadoSincronizacion.SIN_DATOS, null, null));

        MvcTestResult resultado = mvc.get().uri(RUTA).exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).bodyJson().isEqualTo("""
                {"estado": "SIN_DATOS", "versionLocal": null, "actualizadoEn": null}
                """);
    }

    @Test
    void otroMetodoDevuelve405ConProblemDetail() {
        MvcTestResult resultado = mvc.post().uri(RUTA).exchange();

        assertThat(resultado).hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo("urn:catalogo:error:metodo-no-permitido");
    }
}
