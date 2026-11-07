package prog2.serviciocatalogo.catalogo.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ActualizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ReconstruirCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.SincronizarCatalogoUseCase;

// Primero intenta actualizar de a una versión; si no se puede, reconstruye con el snapshot.
@Service
public class SincronizarCatalogoService implements SincronizarCatalogoUseCase {

    private static final Logger log = LoggerFactory.getLogger(SincronizarCatalogoService.class);

    private final ActualizarCatalogoUseCase actualizarCatalogo;
    private final ReconstruirCatalogoUseCase reconstruirCatalogo;

    public SincronizarCatalogoService(
            ActualizarCatalogoUseCase actualizarCatalogo, ReconstruirCatalogoUseCase reconstruirCatalogo) {
        this.actualizarCatalogo = actualizarCatalogo;
        this.reconstruirCatalogo = reconstruirCatalogo;
    }

    // Las fallas se propagan para que quien la llamó (por ejemplo, el listener de Kafka) pueda reintentar.
    @Override
    public ResultadoActualizacion sincronizar() {
        ResultadoActualizacion resultado = actualizarCatalogo.actualizar();
        if (resultado != ResultadoActualizacion.REQUIERE_RECONSTRUCCION) {
            return resultado;
        }
        log.info("No se puede actualizar de a una versión: se reconstruye con el snapshot");
        reconstruirCatalogo.reconstruir();
        log.info("Catálogo reconstruido con el snapshot de la cátedra");
        return ResultadoActualizacion.RECONSTRUIDO;
    }
}
