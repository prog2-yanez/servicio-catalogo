package prog2.serviciocatalogo.catalogo.domain.exception;

public class CatedraNoDisponibleException extends CatedraException {

    public CatedraNoDisponibleException(String motivo, Throwable causa) {
        super("La cátedra no está disponible: " + motivo, causa);
    }
}
