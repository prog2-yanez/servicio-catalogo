package prog2.serviciocatalogo.catalogo.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import prog2.serviciocatalogo.catalogo.domain.model.AvisoCatalogoActualizado;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ProcesarCatalogoActualizadoUseCase;
import tools.jackson.databind.json.JsonMapper;

class CatalogoActualizadoListenerTests {

    private final ProcesarCatalogoActualizadoUseCase procesarAviso = mock(ProcesarCatalogoActualizadoUseCase.class);
    private final CatalogoActualizadoListener listener =
            new CatalogoActualizadoListener(procesarAviso, JsonMapper.builder().build());

    @Test
    void leeElAvisoEIgnoraLosCamposDesconocidos() {
        listener.recibir("""
                {"eventId": "evento-1", "eventType": "CatalogUpdated", "newVersion": 8,
                 "occurredAt": "2026-10-31T12:00:00Z", "source": "catedra", "schemaVersion": 1,
                 "campoNuevo": {"algo": true}}
                """);

        verify(procesarAviso).procesar(new AvisoCatalogoActualizado("evento-1", 8));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{esto no es json",
        "null",
        "{\"eventId\": \"evento-1\", \"eventType\": \"OtroEvento\", \"newVersion\": 8}",
        "{\"eventType\": \"CatalogUpdated\", \"newVersion\": 8}",
        "{\"eventId\": \" \", \"eventType\": \"CatalogUpdated\", \"newVersion\": 8}",
        "{\"eventId\": \"evento-1\", \"eventType\": \"CatalogUpdated\"}"
    })
    void unMensajeQueNoSePuedeUsarSeDescartaSinFallar(String mensaje) {
        assertThatCode(() -> listener.recibir(mensaje)).doesNotThrowAnyException();

        verifyNoInteractions(procesarAviso);
    }

    @Test
    void leerDevuelveElAvisoConSuVersion() {
        assertThat(listener.leer("{\"eventId\":\"e-2\",\"eventType\":\"CatalogUpdated\",\"newVersion\":12}"))
                .contains(new AvisoCatalogoActualizado("e-2", 12));
    }
}
