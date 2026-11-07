package prog2.serviciocatalogo.catalogo.infrastructure.catedra;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.exception.CatedraNoDisponibleException;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CambiosCatalogoCatedra;
import prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto.CambiosResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto.SnapshotResponse.CategoryResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto.SnapshotResponse.ProfessionalResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.catedra.dto.SnapshotResponse.WeeklyScheduleResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

// Lee la actualización incremental que la cátedra publica en Redis.
// Solo usa comandos de lectura (GET, HGET, HGETALL): nunca escribe en las claves de la cátedra.
@Component
public class CambiosCatalogoRedisAdapter implements CambiosCatalogoCatedra {

    static final String CLAVE_METADATA = "catedra:sync:metadata";
    static final String PREFIJO_CAMBIOS = "catedra:sync:changes:";
    static final String HASH_CATEGORIAS = "catedra:sync:professional-categories";
    static final String HASH_PROFESIONALES = "catedra:sync:professionals";
    static final String HASH_HORARIOS = "catedra:sync:weekly-schedules";

    private final StringRedisTemplate redis;
    private final JsonMapper json;

    public CambiosCatalogoRedisAdapter(StringRedisTemplate redis, JsonMapper json) {
        this.redis = redis;
        this.json = json;
    }

    // Las dos versiones se leen juntas de la metadata, así no quedan desfasadas entre sí.
    @Override
    public Optional<EstadoVersiones> estadoVersiones() {
        Map<Object, Object> metadata = leer(() -> redis.opsForHash().entries(CLAVE_METADATA));
        Object actual = metadata.get("currentVersion");
        Object masAntigua = metadata.get("oldestAvailableVersion");
        if (actual == null || masAntigua == null) {
            return Optional.empty();
        }
        return Optional.of(new EstadoVersiones(
                Long.parseLong(actual.toString()), Long.parseLong(masAntigua.toString())));
    }

    @Override
    public Optional<CambiosDeVersion> cambios(long version) {
        String valor = leer(() -> redis.opsForValue().get(PREFIJO_CAMBIOS + version));
        return convertir(valor, CambiosResponse.class).map(CambiosResponse::aDominio);
    }

    @Override
    public Optional<CategoriaProfesional> categoria(long id) {
        return entidad(HASH_CATEGORIAS, id, CategoryResponse.class).map(CategoryResponse::aDominio);
    }

    @Override
    public Optional<Profesional> profesional(long id) {
        return entidad(HASH_PROFESIONALES, id, ProfessionalResponse.class).map(ProfessionalResponse::aDominio);
    }

    @Override
    public Optional<HorarioSemanal> horario(long id) {
        return entidad(HASH_HORARIOS, id, WeeklyScheduleResponse.class).map(WeeklyScheduleResponse::aDominio);
    }

    private <T> Optional<T> entidad(String hash, long id, Class<T> tipo) {
        Object valor = leer(() -> redis.opsForHash().get(hash, String.valueOf(id)));
        return convertir(valor == null ? null : valor.toString(), tipo);
    }

    private <T> Optional<T> convertir(String valor, Class<T> tipo) {
        if (valor == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.readValue(valor, tipo));
        } catch (JacksonException e) {
            throw new CatedraNoDisponibleException("dato de Redis con formato inesperado", null);
        }
    }

    // Un Redis caído o sin permisos se informa igual que la cátedra caída por REST.
    private static <T> T leer(Supplier<T> lectura) {
        try {
            return lectura.get();
        } catch (DataAccessException e) {
            throw new CatedraNoDisponibleException("Redis " + e.getClass().getSimpleName(), null);
        }
    }
}
