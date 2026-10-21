package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.Instant;
import java.util.Optional;

public record SituacionSincronizacion(EstadoSincronizacion estado, Long versionLocal, Instant actualizadoEn) {

    // Reconstruyendo y error mandan sobre los datos guardados; si no, el estado sale de tener o no una versión.
    public static SituacionSincronizacion desde(EstadoSincronizacion registrado, Optional<VersionCatalogo> version) {
        Long numero = version.map(VersionCatalogo::numero).orElse(null);
        Instant actualizadoEn = version.map(VersionCatalogo::actualizadoEn).orElse(null);
        EstadoSincronizacion estado = switch (registrado) {
            case RECONSTRUYENDO, ERROR -> registrado;
            case SIN_DATOS, SINCRONIZADO -> version.isPresent()
                    ? EstadoSincronizacion.SINCRONIZADO
                    : EstadoSincronizacion.SIN_DATOS;
        };
        return new SituacionSincronizacion(estado, numero, actualizadoEn);
    }
}
