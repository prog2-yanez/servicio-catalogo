package prog2.serviciocatalogo.catalogo.domain.model;

import java.time.Instant;

public record VersionCatalogo(long numero, Instant actualizadoEn) {

    public boolean esAnteriorA(long otraVersion) {
        return numero < otraVersion;
    }
}
