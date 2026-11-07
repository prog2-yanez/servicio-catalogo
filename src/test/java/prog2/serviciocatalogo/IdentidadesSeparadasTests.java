package prog2.serviciocatalogo;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

// Las dos identidades del proyecto no se mezclan (ADR 0003): el JWT técnico de la cátedra lo usa solo el backend
// para hablar con la cátedra, no sirve para consultar el catálogo y nunca sale hacia la app.
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "catedra.jwt=" + IdentidadesSeparadasTests.JWT_TECNICO_CONFIGURADO)
@AutoConfigureMockMvc
class IdentidadesSeparadasTests {

    // Valor ficticio del JWT técnico, solo para buscarlo en las respuestas.
    static final String JWT_TECNICO_CONFIGURADO = "jwt-tecnico-de-prueba-que-nunca-sale-del-backend";

    // La misma clave ficticia de src/test/resources/config/application.properties.
    private static final String CLAVE_DEL_CATALOGO = "clave-de-pruebas-que-no-se-usa-en-ningun-entorno";

    // La cátedra firma con su propia clave, que el catálogo no conoce.
    private static final String CLAVE_DE_LA_CATEDRA =
            "clave-propia-de-la-catedra-que-el-catalogo-no-conoce-y-tiene-mas-de-64-caracteres";

    private static final String ROL_DE_LA_CATEDRA = "ROLE_STUDENT_CLIENT";

    @Autowired
    private MockMvcTester mvc;

    @Test
    void unTokenConLaFormaDelDeLaCatedraNoSirveParaConsultar() {
        String tokenTecnico = token(CLAVE_DE_LA_CATEDRA, MacAlgorithm.HS512, null, ROL_DE_LA_CATEDRA);

        verificarRechazo(consultarCon(tokenTecnico), HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    // Segunda barrera: aunque estuviera firmado con nuestra clave, no lo emitió turnos.
    @Test
    void sinElEmisorDeTurnosSeRechazaAunqueLaFirmaSeaValida() {
        String sinEmisor = token(CLAVE_DEL_CATALOGO, MacAlgorithm.HS256, null, ROL_DE_LA_CATEDRA);

        verificarRechazo(consultarCon(sinEmisor), HttpStatus.UNAUTHORIZED, "token-invalido");
    }

    // Tercera barrera: aunque pasara la firma y el emisor, el rol técnico no es el de un usuario.
    @Test
    void conElRolTecnicoSeRechazaAunqueLaFirmaYElEmisorSeanValidos() {
        String conRolTecnico = token(CLAVE_DEL_CATALOGO, MacAlgorithm.HS256, "servicio-turnos", ROL_DE_LA_CATEDRA);

        verificarRechazo(consultarCon(conRolTecnico), HttpStatus.FORBIDDEN, "sin-permiso");
    }

    // Ninguna respuesta del catálogo, exitosa o de error, incluye el JWT técnico configurado.
    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/categorias", "/api/v1/categorias/999999", "/api/v1/profesionales",
        "/api/v1/profesionales/999999", "/api/v1/profesionales?size=500", "/api/v1/sincronizacion/estado",
        "/api/v1/no-existe", "/actuator/health"})
    void elJwtTecnicoNuncaSaleEnUnaRespuesta(String ruta) {
        MvcTestResult resultado = mvc.get().uri(ruta)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(CLAVE_DEL_CATALOGO, MacAlgorithm.HS256,
                        "servicio-turnos", "ROLE_USER"))
                .exchange();

        assertThat(resultado).bodyText().doesNotContain(JWT_TECNICO_CONFIGURADO);
        resultado.getResponse().getHeaderNames().forEach(nombre ->
                assertThat(resultado.getResponse().getHeaders(nombre)).noneMatch(
                        valor -> valor.contains(JWT_TECNICO_CONFIGURADO)));
    }

    // Actuator solo publica el health: la configuración (y con ella el JWT técnico) no se puede leer.
    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/configprops"})
    void laConfiguracionNoSePublica(String ruta) {
        MvcTestResult resultado = mvc.get().uri(ruta)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(CLAVE_DEL_CATALOGO, MacAlgorithm.HS256,
                        "servicio-turnos", "ROLE_USER"))
                .exchange();

        assertThat(resultado.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(resultado).bodyText().doesNotContain(JWT_TECNICO_CONFIGURADO);
    }

    @Test
    void laListaDeActuatorSoloOfreceElHealth() {
        MvcTestResult resultado = mvc.get().uri("/actuator")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(CLAVE_DEL_CATALOGO, MacAlgorithm.HS256,
                        "servicio-turnos", "ROLE_USER"))
                .exchange();

        assertThat(resultado).bodyText().contains("health").doesNotContain("env", "configprops");
    }

    private MvcTestResult consultarCon(String token) {
        return mvc.get().uri("/api/v1/categorias").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).exchange();
    }

    private void verificarRechazo(MvcTestResult resultado, HttpStatus estado, String codigo) {
        assertThat(resultado).hasStatus(estado);
        assertThat(resultado).bodyJson().extractingPath("$.type").isEqualTo("urn:catalogo:error:" + codigo);
    }

    private static String token(String clave, MacAlgorithm algoritmo, String emisor, String roles) {
        String nombreAlgoritmo = algoritmo == MacAlgorithm.HS512 ? "HmacSHA512" : "HmacSHA256";
        SecretKeySpec secreto = new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), nombreAlgoritmo);
        Instant ahora = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .subject("cuenta-tecnica-del-grupo")
                .claim("auth", roles)
                .issuedAt(ahora)
                // Como el de la cátedra, vale un año.
                .expiresAt(ahora.plus(365, ChronoUnit.DAYS));
        if (emisor != null) {
            claims.issuer(emisor);
        }
        return new NimbusJwtEncoder(new ImmutableSecret<>(secreto))
                .encode(JwtEncoderParameters.from(JwsHeader.with(algoritmo).build(), claims.build()))
                .getTokenValue();
    }
}
