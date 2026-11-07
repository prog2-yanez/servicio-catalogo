package prog2.serviciocatalogo.catalogo.domain.model;

// Versiones que publica la cátedra: la última y la más vieja de la que todavía guarda los cambios.
public record EstadoVersiones(long actual, long masAntigua) {

    public boolean hayNovedadesPara(long versionLocal) {
        return actual > versionLocal;
    }

    // Si la copia local quedó más vieja que lo que la cátedra conserva, faltan cambios y hay que reconstruir.
    public boolean permiteActualizarDesde(long versionLocal) {
        return versionLocal >= masAntigua;
    }
}
