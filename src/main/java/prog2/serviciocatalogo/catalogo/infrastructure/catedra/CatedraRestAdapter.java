package prog2.serviciocatalogo.catalogo.infrastructure.catedra;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraAccesoDenegadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatedraClient;
import prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto.SnapshotResponse;

@Component
public class CatedraRestAdapter implements CatedraClient {

    static final String RUTA_SNAPSHOT = "/api/synchronization/snapshot";

    private final RestClient catedraRestClient;

    public CatedraRestAdapter(RestClient catedraRestClient) {
        this.catedraRestClient = catedraRestClient;
    }

    @Override
    public SnapshotCatalogo obtenerSnapshot() {
        SnapshotResponse respuesta;
        try {
            respuesta = catedraRestClient.get()
                    .uri(RUTA_SNAPSHOT)
                    .retrieve()
                    .onStatus(CatedraRestAdapter::esAccesoDenegado, (pedido, resp) -> {
                        throw new CatedraAccesoDenegadoException(resp.getStatusCode().value());
                    })
                    .onStatus(HttpStatusCode::isError, (pedido, resp) -> {
                        throw new CatedraNoDisponibleException("HTTP " + resp.getStatusCode().value(), null);
                    })
                    .body(SnapshotResponse.class);
        } catch (RestClientException e) {
            // No se encadena la excepción original porque su mensaje incluye la URL de la cátedra.
            throw new CatedraNoDisponibleException(e.getClass().getSimpleName(), null);
        }
        if (respuesta == null) {
            throw new CatedraNoDisponibleException("respuesta vacía", null);
        }
        return respuesta.aDominio();
    }

    private static boolean esAccesoDenegado(HttpStatusCode estado) {
        return estado.value() == HttpStatus.UNAUTHORIZED.value() || estado.value() == HttpStatus.FORBIDDEN.value();
    }
}
