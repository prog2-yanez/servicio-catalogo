package prog2.serviciocatalogo;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

// Con un origen de navegador permitido (ficticio), como si hubiera un cliente web además de la app Android.
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "cors.origenes-permitidos=https://panel.ejemplo.com")
@AutoConfigureMockMvc
class CorsIntegracionTests {

    private static final String CLAVE_DE_PRUEBA = "clave-de-pruebas-que-no-se-usa-en-ningun-entorno";
    private static final String PERMITIDO = "https://panel.ejemplo.com";
    private static final String NO_PERMITIDO = "https://otro.ejemplo.com";

    @Autowired
    private MockMvcTester mvc;

    @Test
    void unOrigenPermitidoRecibeLosHeadersDeCors() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias")
                .header(HttpHeaders.ORIGIN, PERMITIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValido()).exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, PERMITIDO);
    }

    // El navegador pregunta antes de mandar el Authorization; esa consulta no lleva token.
    @Test
    void laConsultaPreviaDeUnOrigenPermitidoSeRespondeSinToken() {
        MvcTestResult resultado = mvc.options().uri("/api/v1/profesionales")
                .header(HttpHeaders.ORIGIN, PERMITIDO)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization").exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, PERMITIDO);
        assertThat(resultado).hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET");
    }

    @Test
    void unOrigenNoPermitidoResponde403() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias")
                .header(HttpHeaders.ORIGIN, NO_PERMITIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValido()).exchange();

        verificarRechazo(resultado);
    }

    @Test
    void laConsultaPreviaDeUnOrigenNoPermitidoResponde403() {
        MvcTestResult resultado = mvc.options().uri("/api/v1/categorias")
                .header(HttpHeaders.ORIGIN, NO_PERMITIDO)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET").exchange();

        verificarRechazo(resultado);
    }

    // El catálogo solo se lee: ni siquiera un origen permitido puede pedir otro método.
    @Test
    void unOrigenPermitidoNoPuedeUsarOtroMetodo() {
        MvcTestResult resultado = mvc.options().uri("/api/v1/categorias")
                .header(HttpHeaders.ORIGIN, PERMITIDO)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE").exchange();

        verificarRechazo(resultado);
    }

    @Test
    void sinOrigenNoCambiaNada() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValido()).exchange();

        assertThat(resultado).hasStatusOk();
        assertThat(resultado).doesNotContainHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    private static void verificarRechazo(MvcTestResult resultado) {
        assertThat(resultado).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(resultado).hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(resultado).bodyJson().extractingPath("$.type")
                .isEqualTo("urn:catalogo:error:origen-no-permitido");
        assertThat(resultado).bodyJson().extractingPath("$.title").isEqualTo("Origen no permitido");
        assertThat(resultado).doesNotContainHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    private static String tokenValido() {
        SecretKeySpec secreto = new SecretKeySpec(CLAVE_DE_PRUEBA.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("servicio-turnos")
                .subject("ana")
                .claim("auth", "ROLE_USER")
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(3600))
                .build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(secreto))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
