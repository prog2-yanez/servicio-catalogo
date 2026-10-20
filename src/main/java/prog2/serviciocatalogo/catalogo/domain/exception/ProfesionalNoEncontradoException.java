package prog2.serviciocatalogo.catalogo.domain.exception;

public class ProfesionalNoEncontradoException extends RecursoNoEncontradoException {

    public ProfesionalNoEncontradoException(long id) {
        super("profesional-no-encontrado", "No existe un profesional con id " + id);
    }
}
