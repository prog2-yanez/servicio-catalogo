package prog2.serviciocatalogo.catalogo.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Mensaje CatalogUpdated del topic del catálogo. Solo se leen los campos que se usan;
// los demás (y los que la cátedra agregue en el futuro) se ignoran.
@JsonIgnoreProperties(ignoreUnknown = true)
record CatalogUpdatedMensaje(String eventId, String eventType, Long newVersion) {
}
