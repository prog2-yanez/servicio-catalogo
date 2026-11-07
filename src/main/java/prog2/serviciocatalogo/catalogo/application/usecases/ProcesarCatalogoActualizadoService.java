package prog2.serviciocatalogo.catalogo.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.model.AvisoCatalogoActualizado;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ProcesarCatalogoActualizadoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.SincronizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.EventosProcesados;

@Service
public class ProcesarCatalogoActualizadoService implements ProcesarCatalogoActualizadoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcesarCatalogoActualizadoService.class);

    private final EventosProcesados eventosProcesados;
    private final SincronizarCatalogoUseCase sincronizarCatalogo;

    public ProcesarCatalogoActualizadoService(
            EventosProcesados eventosProcesados, SincronizarCatalogoUseCase sincronizarCatalogo) {
        this.eventosProcesados = eventosProcesados;
        this.sincronizarCatalogo = sincronizarCatalogo;
    }

    // El evento se registra después de sincronizar: si el servicio se cae en el medio, Kafka lo vuelve a
    // entregar y la sincronización da AL_DIA, así que repetirla no tiene efectos. Si falla, se propaga
    // sin registrar para que se reintente.
    @Override
    public boolean procesar(AvisoCatalogoActualizado aviso) {
        if (eventosProcesados.yaFueProcesado(aviso.eventId())) {
            log.info("Aviso {} repetido: se descarta", aviso.eventId());
            return false;
        }
        ResultadoActualizacion resultado = sincronizarCatalogo.sincronizar();
        eventosProcesados.registrar(aviso.eventId(), AvisoCatalogoActualizado.TIPO);
        log.info("Aviso {} de la versión {} procesado: {}", aviso.eventId(), aviso.nuevaVersion(), resultado);
        return true;
    }
}
