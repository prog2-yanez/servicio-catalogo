package prog2.serviciocatalogo.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catedra")
public record CatedraProperties(String jwt, Api api) {

    public record Api(String url, Duration timeoutConexion, Duration timeoutLectura) {
    }
}
