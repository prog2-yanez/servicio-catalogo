package prog2.serviciocatalogo;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

// Con la aplicación entera y tokens firmados de verdad, como los que emite turnos (ADR 0003).
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadIntegracionTests {

    // La misma clave ficticia de src/test/resources/config/application.properties.
    private static final String CLAVE_DE_PRUEBA = "clave-de-pruebas-que-no-se-usa-en-ningun-entorno";
    private static final String OTRA_CLAVE = "otra-clave-que-no-es-la-del-catalogo-ni-de-turnos";

    @Autowired
    private MockMvcTester mvc;

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/categorias", "/api/v1/categorias/1", "/api/v1/profesionales",
        "/api/v1/profesionales/15", "/api/v1/sincronizacion/estado"})
    void sinTokenCadaEndpointResponde401(String ruta) {
        MvcTestResult resultado = mvc.get().uri(ruta).exchange();

        verificarProblema(resultado, HttpStatus.UNAUTHORIZED, "no-autenticado");
    }

    @Test
    void conUnTokenValidoDeTurnosSePuedeConsultar() {
        MvcTestResult resultado = conToken(token(CLAVE_DE_PRUEBA, "servicio-turnos", "ROLE_USER", 3600));

        assertThat(resultado).hasStatusOk();
    }

    @Test
    void unTokenVencidoResponde401TokenInvalido() {
        verificarProblema(conToken(token(CLAVE_DE_PRUEBA, "servicio-turnos", "ROLE_USER", -60)),
                HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    @Test
    void unTokenFirmadoConOtraClaveResponde401TokenInvalido() {
        verificarProblema(conToken(token(OTRA_CLAVE, "servicio-turnos", "ROLE_USER", 3600)),
                HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    @Test
    void unTokenDeOtroEmisorResponde401TokenInvalido() {
        verificarProblema(conToken(token(CLAVE_DE_PRUEBA, "otro-sistema", "ROLE_USER", 3600)),
                HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    @Test
    void unTokenQueNoEsUnJwtResponde401TokenInvalido() {
        verificarProblema(conToken("esto-no-es-un-jwt"), HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    @Test
    void unTokenValidoSinElRolDeUsuarioResponde403() {
        verificarProblema(conToken(token(CLAVE_DE_PRUEBA, "servicio-turnos", "ROLE_OTRO", 3600)),
                HttpStatus.FORBIDDEN, "sin-permiso");
    }

    @Test
    void otroEsquemaDeAutenticacionResponde401() {
        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXN1YXJpbzpjbGF2ZQ==").exchange();

        verificarProblema(resultado, HttpStatus.UNAUTHORIZED, "no-autenticado");
    }

    // Sin CORS_ORIGENES_PERMITIDOS ningún navegador de otro origen puede usar el catálogo.
    @Test
    void sinOrigenesConfiguradosUnNavegadorDeOtroOrigenSeRechaza() {
        String valido = token(CLAVE_DE_PRUEBA, "servicio-turnos", "ROLE_USER", 3600);
        MvcTestResult resultado = mvc.get().uri("/api/v1/categorias")
                .header(HttpHeaders.ORIGIN, "https://panel.ejemplo.com")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + valido).exchange();

        verificarProblema(resultado, HttpStatus.FORBIDDEN, "origen-no-permitido");
    }

    @Test
    void elHealthSiguePublico() {
        assertThat(mvc.get().uri("/actuator/health").exchange()).hasStatusOk();
    }

    private MvcTestResult conToken(String token) {
        return mvc.get().uri("/api/v1/categorias").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).exchange();
    }

    // Arma un token con el mismo formato que emite turnos: sub, userId, auth e iss.
    private static String token(String clave, String emisor, String roles, long segundosDeVida) {
        SecretKeySpec secreto = new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Instant ahora = Instant.now();
        Instant emitido = segundosDeVida > 0 ? ahora : ahora.minus(Duration.ofHours(2));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(emisor)
                .subject("ana")
                .claim("userId", 4)
                .claim("auth", roles)
                .issuedAt(emitido)
                .expiresAt(ahora.plusSeconds(segundosDeVida))
                .build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(secreto))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    private static void verificarProblema(MvcTestResult resultado, HttpStatus estado, String codigo) {
        assertThat(resultado).hasStatus(estado);
        assertThat(resultado).hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(resultado).bodyJson().extractingPath("$.type").isEqualTo("urn:catalogo:error:" + codigo);
        assertThat(resultado).bodyJson().extractingPath("$.status").isEqualTo(estado.value());
    }
}
