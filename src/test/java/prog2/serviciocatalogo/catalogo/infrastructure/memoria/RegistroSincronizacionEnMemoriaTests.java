package prog2.serviciocatalogo.catalogo.infrastructure.memoria;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;

class RegistroSincronizacionEnMemoriaTests {

    private final RegistroSincronizacionEnMemoria registro = new RegistroSincronizacionEnMemoria();

    @Test
    void empiezaSinDatos() {
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.SIN_DATOS);
    }

    @Test
    void guardaElUltimoEstadoMarcado() {
        registro.marcarReconstruyendo();
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.RECONSTRUYENDO);

        registro.marcarError();
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.ERROR);

        registro.marcarSincronizado();
        assertThat(registro.estadoActual()).isEqualTo(EstadoSincronizacion.SINCRONIZADO);
    }
}
