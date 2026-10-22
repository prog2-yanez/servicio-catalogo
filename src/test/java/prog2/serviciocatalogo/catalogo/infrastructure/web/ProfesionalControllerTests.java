package prog2.serviciocatalogo.catalogo.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;

@WebMvcTest(controllers = ProfesionalController.class)
class ProfesionalControllerTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BuscarProfesionalesUseCase buscarProfesionales;

    @Test
    void pasaLosFiltrosYDevuelveLaPaginaConElFormatoDelContrato() {
        Profesional ana = new Profesional(10L, 1L, "Ana", "Gomez", true, CREADO, CREADO);
        when(buscarProfesionales.buscar(new FiltroProfesionales(1L, "ana"), new PedidoPagina(2, 5)))
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

        verify(buscarProfesionales).buscar(new FiltroProfesionales(null, null), new PedidoPagina(0, 20));
    }

    @Test
    void unTamanioFueraDeRangoDevuelve400ConElParametroQueFallo() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales?size=500").exchange();

        assertThat(resultado).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultado).bodyJson().extractingPath("$.detail").asString().contains("size", "100");
        verifyNoInteractions(buscarProfesionales);
    }

    @Test
    void sinDatosDevuelve503() {
        when(buscarProfesionales.buscar(any(), any())).thenThrow(new CatalogoSinDatosException());

        MvcTestResult resultado = mvc.get().uri("/api/v1/profesionales").exchange();

        assertThat(resultado).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resultado).bodyJson().extractingPath("$.type").isEqualTo("urn:catalogo:error:catalogo-sin-datos");
    }
}
