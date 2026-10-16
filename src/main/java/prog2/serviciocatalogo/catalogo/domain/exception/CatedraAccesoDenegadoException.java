package prog2.serviciocatalogo.catalogo.domain.exception;

public class CatedraAccesoDenegadoException extends CatedraException {

    public CatedraAccesoDenegadoException(int estadoHttp) {
        super("La cátedra rechazó las credenciales del servicio (HTTP " + estadoHttp + ")");
    }
}
