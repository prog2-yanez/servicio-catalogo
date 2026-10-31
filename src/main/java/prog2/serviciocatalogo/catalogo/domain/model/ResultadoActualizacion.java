package prog2.serviciocatalogo.catalogo.domain.model;

// Cómo terminó un intento de actualización incremental.
public enum ResultadoActualizacion {

    // La copia local ya tenía la última versión de la cátedra.
    AL_DIA,

    // Se aplicaron todas las versiones nuevas, en orden.
    ACTUALIZADO,

    // No se puede seguir de a una versión: faltan datos o versiones, hay que pedir el snapshot.
    REQUIERE_RECONSTRUCCION
}
