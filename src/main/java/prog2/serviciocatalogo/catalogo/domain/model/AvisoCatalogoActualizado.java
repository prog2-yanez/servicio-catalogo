package prog2.serviciocatalogo.catalogo.domain.model;

// Aviso de la cátedra de que publicó una versión nueva del catálogo.
public record AvisoCatalogoActualizado(String eventId, long nuevaVersion) {

    public static final String TIPO = "CatalogUpdated";
}
