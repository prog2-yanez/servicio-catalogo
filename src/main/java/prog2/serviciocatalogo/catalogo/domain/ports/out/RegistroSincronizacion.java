package prog2.serviciocatalogo.catalogo.domain.ports.out;

import prog2.serviciocatalogo.catalogo.domain.model.EstadoSincronizacion;

public interface RegistroSincronizacion {

    void marcarReconstruyendo();

    void marcarSincronizado();

    void marcarError();

    EstadoSincronizacion estadoActual();
}
