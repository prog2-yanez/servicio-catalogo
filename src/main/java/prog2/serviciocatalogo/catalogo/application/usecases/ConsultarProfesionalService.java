package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalDeshabilitadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalNoEncontradoException;
import prog2.serviciocatalogo.catalogo.domain.model.AgendaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarProfesionalUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

@Service
@Transactional(readOnly = true)
public class ConsultarProfesionalService implements ConsultarProfesionalUseCase {

    private final GuardiaCatalogo guardiaCatalogo;
    private final ConsultaProfesionalesRepository consultaProfesionales;

    public ConsultarProfesionalService(
            GuardiaCatalogo guardiaCatalogo, ConsultaProfesionalesRepository consultaProfesionales) {
        this.guardiaCatalogo = guardiaCatalogo;
        this.consultaProfesionales = consultaProfesionales;
    }

    // Inexistente y deshabilitado son errores distintos: Turnos no puede dar turnos con ninguno de los dos.
    @Override
    public AgendaProfesional obtenerConAgenda(long id) {
        VersionCatalogo version = guardiaCatalogo.exigirDatos();
        Profesional profesional = consultaProfesionales.buscarPorId(id)
                .orElseThrow(() -> new ProfesionalNoEncontradoException(id));
        if (!profesional.habilitado()) {
            throw new ProfesionalDeshabilitadoException(id);
        }
        return new AgendaProfesional(profesional, consultaProfesionales.horariosHabilitados(id), version.numero());
    }
}
