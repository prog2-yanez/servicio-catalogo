package prog2.serviciocatalogo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class JwtPropertiesTests {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"corta", "una-clave-de-treinta-y-un-chars"})
    void sinUnaClaveDeAlMenos32CaracteresElServicioNoArranca(String secreto) {
        assertThatThrownBy(() -> new JwtProperties(secreto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_SECRETO tiene que tener al menos 32 caracteres");
    }

    @Test
    void conUnaClaveSuficienteDevuelveSusBytes() {
        String secreto = "una-clave-de-treinta-y-dos-chars";

        assertThat(new JwtProperties(secreto).secretoEnBytes())
                .isEqualTo(secreto.getBytes(StandardCharsets.UTF_8));
    }
}
