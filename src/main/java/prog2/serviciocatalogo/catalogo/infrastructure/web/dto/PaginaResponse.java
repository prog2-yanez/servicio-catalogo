package prog2.serviciocatalogo.catalogo.infrastructure.web.dto;

import java.util.List;
import java.util.function.Function;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;

// Mismo formato de página que usa Spring Data, como define el ADR 0002.
public record PaginaResponse<T>(List<T> content, Metadatos page) {

    public record Metadatos(int size, int number, long totalElements, int totalPages) {
    }

    public static <D, T> PaginaResponse<T> desde(Pagina<D> pagina, Function<D, T> conversion) {
        Pagina<T> convertida = pagina.map(conversion);
        return new PaginaResponse<>(convertida.contenido(), new Metadatos(
                convertida.tamanio(), convertida.numero(), convertida.totalElementos(), convertida.totalPaginas()));
    }
}
