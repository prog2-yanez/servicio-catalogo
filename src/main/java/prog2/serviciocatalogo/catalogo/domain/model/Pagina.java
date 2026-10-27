package prog2.serviciocatalogo.catalogo.domain.model;

import java.util.List;
import java.util.function.Function;

public record Pagina<T>(List<T> contenido, int numero, int tamanio, long totalElementos) {

    public Pagina {
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return (int) ((totalElementos + tamanio - 1) / tamanio);
    }

    public <R> Pagina<R> map(Function<T, R> conversion) {
        return new Pagina<>(contenido.stream().map(conversion).toList(), numero, tamanio, totalElementos);
    }
}
