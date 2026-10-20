package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import java.time.Instant;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.model.SituacionSincronizacion;

public record EstadoSincronizacionResponse(EstadoSincronizacion estado, Long versionLocal, Instant actualizadoEn) {

    public static EstadoSincronizacionResponse desde(SituacionSincronizacion situacion) {
        return new EstadoSincronizacionResponse(
                situacion.estado(), situacion.versionLocal(), situacion.actualizadoEn());
    }
}
