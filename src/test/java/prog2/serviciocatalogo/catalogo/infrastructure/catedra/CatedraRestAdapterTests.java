package prog2.serviciocatalogo.catalogo.infrastructure.catedra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.SocketTimeoutException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraAccesoDenegadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;

class CatedraRestAdapterTests {

    private static final String URL_BASE = "http://catedra.test";
    private static final String URL_SNAPSHOT = URL_BASE + CatedraRestAdapter.RUTA_SNAPSHOT;
    private static final String JWT = "jwt-de-prueba";

    private MockRestServiceServer servidor;
    private CatedraRestAdapter adaptador;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(URL_BASE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + JWT);
        servidor = MockRestServiceServer.bindTo(builder).build();
        adaptador = new CatedraRestAdapter(builder.build());
    }

    @Test
    void convierteElSnapshotDeLaCatedraAlDominio() {
        servidor.expect(requestTo(URL_SNAPSHOT))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT))
                .andRespond(withSuccess(SNAPSHOT_EJEMPLO, MediaType.APPLICATION_JSON));

        SnapshotCatalogo snapshot = adaptador.obtenerSnapshot();

        assertThat(snapshot.version()).isEqualTo(7);
        assertThat(snapshot.generadoEn()).isEqualTo(Instant.parse("2026-09-10T13:44:59Z"));
        assertThat(snapshot.categorias()).singleElement()
                .satisfies(categoria -> assertThat(categoria.nombre()).isEqualTo("Clinica medica"));
        assertThat(snapshot.profesionales()).singleElement()
                .satisfies(profesional -> assertThat(profesional.nombreCompleto()).isEqualTo("Ana Gomez"));
        assertThat(snapshot.horarios()).singleElement().isEqualTo(new HorarioSemanal(
                21L, 15L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true,
                Instant.parse("2026-07-01T12:00:00Z"), Instant.parse("2026-09-10T13:40:00Z")));
        servidor.verify();
    }

    @Test
    void aceptaUnSnapshotConListasVacias() {
        servidor.expect(requestTo(URL_SNAPSHOT)).andRespond(withSuccess("""
                {"snapshotVersion": 1, "generatedAt": "2026-09-10T13:44:59Z",
                 "professionalCategories": [], "professionals": [], "weeklySchedules": []}
                """, MediaType.APPLICATION_JSON));

        SnapshotCatalogo snapshot = adaptador.obtenerSnapshot();

        assertThat(snapshot.version()).isEqualTo(1);
        assertThat(snapshot.categorias()).isEmpty();
        assertThat(snapshot.profesionales()).isEmpty();
        assertThat(snapshot.horarios()).isEmpty();
    }

    @Test
    void traduceUn401AAccesoDenegado() {
        servidor.expect(requestTo(URL_SNAPSHOT)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(adaptador::obtenerSnapshot)
                .isInstanceOf(CatedraAccesoDenegadoException.class)
                .hasMessageContaining("401");
    }

    @Test
    void traduceUn403AAccesoDenegado() {
        servidor.expect(requestTo(URL_SNAPSHOT)).andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThatThrownBy(adaptador::obtenerSnapshot)
                .isInstanceOf(CatedraAccesoDenegadoException.class)
                .hasMessageContaining("403");
    }

    @Test
    void traduceUn503ANoDisponible() {
        servidor.expect(requestTo(URL_SNAPSHOT)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(adaptador::obtenerSnapshot)
                .isInstanceOf(CatedraNoDisponibleException.class)
                .hasMessageContaining("503");
    }

    @Test
    void traduceUnTimeoutANoDisponibleSinMostrarDatosSensibles() {
        servidor.expect(requestTo(URL_SNAPSHOT)).andRespond(withException(new SocketTimeoutException()));

        assertThatThrownBy(adaptador::obtenerSnapshot)
                .isInstanceOf(CatedraNoDisponibleException.class)
                .hasNoCause()
                .message()
                .doesNotContain(URL_BASE)
                .doesNotContain(JWT);
    }

    private static final String SNAPSHOT_EJEMPLO = """
            {
              "snapshotVersion": 7,
              "generatedAt": "2026-09-10T13:44:59Z",
              "professionalCategories": [
                {"id": 1, "name": "Clinica medica", "description": "Atencion clinica general", "enabled": true,
                 "createdAt": "2026-07-01T12:00:00Z", "updatedAt": "2026-09-10T13:40:00Z"}
              ],
              "professionals": [
                {"id": 15, "categoryId": 1, "firstName": "Ana", "lastName": "Gomez", "enabled": true,
                 "createdAt": "2026-07-01T12:00:00Z", "updatedAt": "2026-09-10T13:40:00Z"}
              ],
              "weeklySchedules": [
                {"id": 21, "professionalId": 15, "dayOfWeek": "MONDAY", "startTime": "09:00:00",
                 "endTime": "12:00:00", "slotDurationMinutes": 30, "enabled": true,
                 "createdAt": "2026-07-01T12:00:00Z", "updatedAt": "2026-09-10T13:40:00Z",
                 "campoNuevo": "ignorado"}
              ]
            }
            """;
}
