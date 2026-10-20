package prog2.serviciocatalogo.catalogo.domain.exception;

public class CategoriaNoEncontradaException extends RecursoNoEncontradoException {

    public CategoriaNoEncontradaException(long id) {
        super("No existe una categoría con id " + id);
    }
}
