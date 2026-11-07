package prog2.serviciocatalogo.config;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Clave con la que turnos firma el JWT de los usuarios (ADR 0003). Tiene que ser la misma en los dos servicios.
// Sin una clave de al menos 256 bits el servicio no arranca.
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secreto) {

    // Solo se aceptan tokens emitidos por turnos.
    public static final String EMISOR = "servicio-turnos";

    static final int LARGO_MINIMO_SECRETO = 32;

    public JwtProperties {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < LARGO_MINIMO_SECRETO) {
            throw new IllegalStateException(
                    "JWT_SECRETO tiene que tener al menos " + LARGO_MINIMO_SECRETO + " caracteres");
        }
    }

    public byte[] secretoEnBytes() {
        return secreto.getBytes(StandardCharsets.UTF_8);
    }
}
