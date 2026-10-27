package prog2.serviciocatalogo.catalogo.domain.model;

public record PedidoPagina(int numero, int tamanio) {

    public static final int TAMANIO_MAXIMO = 100;

    public PedidoPagina {
        if (numero < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (tamanio < 1 || tamanio > TAMANIO_MAXIMO) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y " + TAMANIO_MAXIMO);
        }
    }
}
