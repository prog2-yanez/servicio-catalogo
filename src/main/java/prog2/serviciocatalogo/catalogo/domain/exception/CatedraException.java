package prog2.serviciocatalogo.catalogo.domain.exception;

public abstract class CatedraException extends RuntimeException {

    protected CatedraException(String mensaje) {
        super(mensaje);
    }

    protected CatedraException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
