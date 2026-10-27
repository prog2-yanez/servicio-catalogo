package prog2.serviciocatalogo.catalogo.domain.exception;

public class ProfesionalDeshabilitadoException extends RuntimeException {

    public ProfesionalDeshabilitadoException(long id) {
        super("El profesional con id " + id + " está deshabilitado");
    }
}
