package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import prog2.serviciocatalogo.catalogo.domain.model.SituacionSincronizacion;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarEstadoSincronizacionUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.RegistroSincronizacion;

@Service
public class ConsultarEstadoSincronizacionService implements ConsultarEstadoSincronizacionUseCase {

    private final RegistroSincronizacion registroSincronizacion;
    private final CatalogoRepository catalogoRepository;

    public ConsultarEstadoSincronizacionService(
            RegistroSincronizacion registroSincronizacion, CatalogoRepository catalogoRepository) {
        this.registroSincronizacion = registroSincronizacion;
        this.catalogoRepository = catalogoRepository;
    }

    @Override
    public SituacionSincronizacion consultar() {
        return SituacionSincronizacion.desde(
                registroSincronizacion.estadoActual(), catalogoRepository.obtenerVersion());
    }
}
