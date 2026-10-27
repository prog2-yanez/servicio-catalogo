package prog2.serviciocatalogo.catalogo.domain.exception;

public abstract class RecursoNoEncontradoException extends RuntimeException {

    private final String codigo;

    protected RecursoNoEncontradoException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
