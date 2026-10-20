package prog2.serviciocatalogo.catalogo.domain.exception;

public class CatalogoSinDatosException extends RuntimeException {

    public CatalogoSinDatosException() {
        super("El catálogo todavía no tiene datos sincronizados");
    }
}
