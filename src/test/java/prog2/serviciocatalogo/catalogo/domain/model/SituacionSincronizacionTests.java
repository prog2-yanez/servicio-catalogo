package prog2.serviciocatalogo.catalogo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SituacionSincronizacionTests {

    private static final VersionCatalogo VERSION_7 =
            new VersionCatalogo(7, Instant.parse("2026-10-19T13:40:00Z"));

    @Test
    void sinVersionYSinNadaRegistradoEstaSinDatos() {
        SituacionSincronizacion situacion =
                SituacionSincronizacion.desde(EstadoSincronizacion.SIN_DATOS, Optional.empty());

        assertThat(situacion).isEqualTo(new SituacionSincronizacion(EstadoSincronizacion.SIN_DATOS, null, null));
    }

    @Test
    void conVersionEstaSincronizado() {
        SituacionSincronizacion situacion =
                SituacionSincronizacion.desde(EstadoSincronizacion.SIN_DATOS, Optional.of(VERSION_7));

        assertThat(situacion.estado()).isEqualTo(EstadoSincronizacion.SINCRONIZADO);
        assertThat(situacion.versionLocal()).isEqualTo(7);
        assertThat(situacion.actualizadoEn()).isEqualTo(VERSION_7.actualizadoEn());
    }

    @Test
    void reconstruyendoTienePrioridadSobreLaVersionGuardada() {
        SituacionSincronizacion situacion =
                SituacionSincronizacion.desde(EstadoSincronizacion.RECONSTRUYENDO, Optional.of(VERSION_7));

        assertThat(situacion.estado()).isEqualTo(EstadoSincronizacion.RECONSTRUYENDO);
        assertThat(situacion.versionLocal()).isEqualTo(7);
    }

    @Test
    void conErrorInformaLaUltimaVersionQueSeAplico() {
        SituacionSincronizacion situacion =
                SituacionSincronizacion.desde(EstadoSincronizacion.ERROR, Optional.of(VERSION_7));

        assertThat(situacion.estado()).isEqualTo(EstadoSincronizacion.ERROR);
        assertThat(situacion.versionLocal()).isEqualTo(7);
    }

    @Test
    void sincronizadoSinVersionSeInformaComoSinDatos() {
        SituacionSincronizacion situacion =
                SituacionSincronizacion.desde(EstadoSincronizacion.SINCRONIZADO, Optional.empty());

        assertThat(situacion.estado()).isEqualTo(EstadoSincronizacion.SIN_DATOS);
    }
}
