package prog2.serviciocatalogo.catalogo.infrastructure.catedra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import prog2.serviciocatalogo.RedisTestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import tools.jackson.databind.json.JsonMapper;

// Los datos de prueba copian el formato real que publica la cátedra en Redis.
@DataRedisTest
@Import(RedisTestcontainersConfiguration.class)
class CambiosCatalogoRedisTests {

    private static final Instant CREADO = Instant.parse("2026-09-01T03:00:00Z");

    @Autowired
    private StringRedisTemplate redis;

    private CambiosCatalogoRedisAdapter adaptador;

    @BeforeEach
    void cargarDatosDeLaCatedra() {
        redis.getRequiredConnectionFactory().getConnection().serverCommands().flushDb();
        adaptador = new CambiosCatalogoRedisAdapter(redis, JsonMapper.builder().build());
        redis.opsForHash().putAll(CambiosCatalogoRedisAdapter.CLAVE_METADATA, Map.of(
                "currentVersion", "7", "oldestAvailableVersion", "4", "publishedAt", "2026-09-22T00:08:12Z"));
        redis.opsForValue().set(CambiosCatalogoRedisAdapter.PREFIJO_CAMBIOS + "5", """
                {"version":5,"changes":{"professionalCategories":[107],"professionals":[204,205],
                 "weeklySchedules":[308]}}""");
        redis.opsForHash().put(CambiosCatalogoRedisAdapter.HASH_CATEGORIAS, "107", """
                {"id":107,"name":"Ginecologia","description":"Atencion ginecologica para el escenario academico",
                 "enabled":true,"createdAt":"2026-09-01T03:00:00Z","updatedAt":"2026-09-01T03:00:00Z"}""");
        redis.opsForHash().put(CambiosCatalogoRedisAdapter.HASH_PROFESIONALES, "204", """
                {"id":204,"categoryId":102,"firstName":"Laura","lastName":"Medina","enabled":true,
                 "createdAt":"2026-09-01T03:00:00Z","updatedAt":"2026-09-01T03:00:00Z","campoNuevo":"x"}""");
        redis.opsForHash().put(CambiosCatalogoRedisAdapter.HASH_HORARIOS, "308", """
                {"id":308,"professionalId":205,"dayOfWeek":"TUESDAY","startTime":"17:00:00","endTime":"20:00:00",
                 "slotDurationMinutes":30,"enabled":true,"createdAt":"2026-09-01T03:00:00Z",
                 "updatedAt":"2026-09-01T03:00:00Z"}""");
    }

    @Test
    void leeLasDosVersionesDeLaMetadata() {
        assertThat(adaptador.estadoVersiones()).contains(new EstadoVersiones(7, 4));
    }

    @Test
    void sinMetadataNoHayEstado() {
        redis.delete(CambiosCatalogoRedisAdapter.CLAVE_METADATA);

        assertThat(adaptador.estadoVersiones()).isEmpty();
    }

    @Test
    void leeLosIdsQueCambiaronEnUnaVersion() {
        assertThat(adaptador.cambios(5))
                .contains(new CambiosDeVersion(5, Set.of(107L), Set.of(204L, 205L), Set.of(308L)));
    }

    @Test
    void unaVersionSinCambiosGuardadosDevuelveVacio() {
        assertThat(adaptador.cambios(6)).isEmpty();
    }

    @Test
    void leeCadaEntidadPorSuIdConElFormatoDelSnapshot() {
        assertThat(adaptador.categoria(107)).contains(new CategoriaProfesional(107L, "Ginecologia",
                "Atencion ginecologica para el escenario academico", true, CREADO, CREADO));
        assertThat(adaptador.profesional(204))
                .contains(new Profesional(204L, 102L, "Laura", "Medina", true, CREADO, CREADO));
        assertThat(adaptador.horario(308)).contains(new HorarioSemanal(308L, 205L, DayOfWeek.TUESDAY,
                LocalTime.of(17, 0), LocalTime.of(20, 0), 30, true, CREADO, CREADO));
    }

    @Test
    void unaEntidadQueNoEstaEnElHashDevuelveVacio() {
        assertThat(adaptador.categoria(999)).isEmpty();
        assertThat(adaptador.profesional(999)).isEmpty();
        assertThat(adaptador.horario(999)).isEmpty();
    }

    @Test
    void leerNoModificaLasClavesDeLaCatedra() {
        Set<String> antes = redis.keys("catedra:sync:*");

        adaptador.estadoVersiones();
        adaptador.cambios(5);
        adaptador.profesional(204);

        assertThat(redis.keys("catedra:sync:*")).isEqualTo(antes);
        assertThat(redis.opsForHash().get(CambiosCatalogoRedisAdapter.CLAVE_METADATA, "currentVersion"))
                .isEqualTo("7");
    }

    @Test
    void unDatoConFormatoInesperadoSeInformaComoCatedraNoDisponible() {
        redis.opsForHash().put(CambiosCatalogoRedisAdapter.HASH_PROFESIONALES, "250", "esto no es json");

        assertThatThrownBy(() -> adaptador.profesional(250)).isInstanceOf(CatedraNoDisponibleException.class);
    }

    @Test
    void unRedisQueNoRespondeSeInformaComoCatedraNoDisponible() {
        LettuceConnectionFactory sinServidor = new LettuceConnectionFactory("localhost", 1);
        sinServidor.afterPropertiesSet();
        sinServidor.start();
        CambiosCatalogoRedisAdapter caido =
                new CambiosCatalogoRedisAdapter(new StringRedisTemplate(sinServidor), JsonMapper.builder().build());

        assertThatThrownBy(caido::estadoVersiones)
                .isInstanceOf(CatedraNoDisponibleException.class)
                .hasMessageNotContaining("localhost");
        sinServidor.destroy();
    }
}
