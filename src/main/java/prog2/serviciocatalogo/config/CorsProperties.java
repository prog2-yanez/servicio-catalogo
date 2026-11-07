package prog2.serviciocatalogo.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Orígenes de navegador que pueden llamar al catálogo. Vacía por defecto: la app Android y turnos no son
// navegadores, así que no necesitan CORS.
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(List<String> origenesPermitidos) {

    public CorsProperties {
        origenesPermitidos = origenesPermitidos == null
                ? List.of()
                : origenesPermitidos.stream().map(String::trim).filter(origen -> !origen.isEmpty()).toList();
    }
}
