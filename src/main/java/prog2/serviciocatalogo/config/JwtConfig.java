package prog2.serviciocatalogo.config;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    // HS256 con la clave compartida con turnos. Valida firma, vencimiento (exp) y emisor (iss); el catálogo no emite
    // tokens, solo los valida.
    @Bean
    public JwtDecoder jwtDecoder(JwtProperties propiedades) {
        NimbusJwtDecoder decodificador = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(propiedades.secretoEnBytes(), "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decodificador.setJwtValidator(JwtValidators.createDefaultWithIssuer(JwtProperties.EMISOR));
        return decodificador;
    }
}
