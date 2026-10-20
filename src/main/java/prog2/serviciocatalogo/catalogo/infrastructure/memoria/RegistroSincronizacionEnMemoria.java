package prog2.serviciocatalogo.catalogo.infrastructure.memoria;

import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

// El estado se pierde al reiniciar: al arrancar, la inicialización del catálogo lo vuelve a marcar.
@Component
public class RegistroSincronizacionEnMemoria implements RegistroSincronizacion {

    private final AtomicReference<EstadoSincronizacion> estado =
            new AtomicReference<>(EstadoSincronizacion.SIN_DATOS);

    @Override
    public void marcarReconstruyendo() {
        estado.set(EstadoSincronizacion.RECONSTRUYENDO);
    }

    @Override
    public void marcarSincronizado() {
        estado.set(EstadoSincronizacion.SINCRONIZADO);
    }

    @Override
    public void marcarError() {
        estado.set(EstadoSincronizacion.ERROR);
    }

    @Override
    public EstadoSincronizacion estadoActual() {
        return estado.get();
    }
}
