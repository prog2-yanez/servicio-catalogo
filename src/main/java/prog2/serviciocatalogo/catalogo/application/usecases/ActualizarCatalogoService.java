package prog2.serviciocatalogo.catalogo.application.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.model.ActualizacionDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.ResultadoActualizacion;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ActualizarCatalogoUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CambiosCatalogoCatedra;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

// Lleva la copia local a la última versión de la cátedra, aplicando las versiones de a una y en orden.
@Service
public class ActualizarCatalogoService implements ActualizarCatalogoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActualizarCatalogoService.class);

    private final CatalogoRepository catalogoRepository;
    private final CambiosCatalogoCatedra cambiosCatedra;
    private final AplicarVersionService aplicarVersion;
    private final RegistroSincronizacion registro;

    public ActualizarCatalogoService(CatalogoRepository catalogoRepository, CambiosCatalogoCatedra cambiosCatedra,
            AplicarVersionService aplicarVersion, RegistroSincronizacion registro) {
        this.catalogoRepository = catalogoRepository;
        this.cambiosCatedra = cambiosCatedra;
        this.aplicarVersion = aplicarVersion;
        this.registro = registro;
    }

    // Sin transacción propia: cada versión tiene la suya, así las que ya se aplicaron quedan aplicadas.
    @Override
    public ResultadoActualizacion actualizar() {
        try {
            ResultadoActualizacion resultado = aplicarVersionesNuevas();
            if (resultado != ResultadoActualizacion.REQUIERE_RECONSTRUCCION) {
                registro.marcarSincronizado();
            }
            return resultado;
        } catch (RuntimeException e) {
            registro.marcarError();
            throw e;
        }
    }

    private ResultadoActualizacion aplicarVersionesNuevas() {
        Optional<VersionCatalogo> local = catalogoRepository.obtenerVersion();
        Optional<EstadoVersiones> estado = cambiosCatedra.estadoVersiones();
        if (local.isEmpty() || estado.isEmpty()) {
            return ResultadoActualizacion.REQUIERE_RECONSTRUCCION;
        }
        long versionLocal = local.get().numero();
        if (!estado.get().hayNovedadesPara(versionLocal)) {
            return ResultadoActualizacion.AL_DIA;
        }
        if (!estado.get().permiteActualizarDesde(versionLocal)) {
            log.warn("La versión local {} es anterior a la más antigua disponible {}",
                    versionLocal, estado.get().masAntigua());
            return ResultadoActualizacion.REQUIERE_RECONSTRUCCION;
        }
        for (long version = versionLocal + 1; version <= estado.get().actual(); version++) {
            Optional<ActualizacionDeVersion> actualizacion = leerCompleta(version);
            if (actualizacion.isEmpty()) {
                log.warn("Faltan datos de la versión {} en la cátedra", version);
                return ResultadoActualizacion.REQUIERE_RECONSTRUCCION;
            }
            aplicarVersion.aplicar(actualizacion.get());
            log.info("Versión {} aplicada ({} cambios)", version, actualizacion.get().cantidad());
        }
        return ResultadoActualizacion.ACTUALIZADO;
    }

    // Todo se lee antes de escribir: si falta un solo dato, la versión no se aplica.
    private Optional<ActualizacionDeVersion> leerCompleta(long version) {
        Optional<CambiosDeVersion> cambios = cambiosCatedra.cambios(version);
        if (cambios.isEmpty()) {
            return Optional.empty();
        }
        var categorias = todas(cambios.get().idsCategorias(), cambiosCatedra::categoria);
        var profesionales = todas(cambios.get().idsProfesionales(), cambiosCatedra::profesional);
        var horarios = todas(cambios.get().idsHorarios(), cambiosCatedra::horario);
        if (categorias.isEmpty() || profesionales.isEmpty() || horarios.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ActualizacionDeVersion(version, categorias.get(), profesionales.get(), horarios.get()));
    }

    private static <T> Optional<List<T>> todas(Set<Long> ids, LongFunction<Optional<T>> buscar) {
        List<T> encontradas = new ArrayList<>();
        for (long id : ids.stream().sorted().toList()) {
            Optional<T> entidad = buscar.apply(id);
            if (entidad.isEmpty()) {
                return Optional.empty();
            }
            encontradas.add(entidad.get());
        }
        return Optional.of(encontradas);
    }
}
