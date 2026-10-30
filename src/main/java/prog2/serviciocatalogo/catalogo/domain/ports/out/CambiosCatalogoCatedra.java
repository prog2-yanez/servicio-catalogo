package prog2.serviciocatalogo.catalogo.domain.ports.out;

import java.util.Optional;
import prog2.serviciocatalogo.catalogo.domain.model.CambiosDeVersion;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.EstadoVersiones;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;

// Lectura de la actualización incremental que publica la cátedra. Un vacío significa que el dato no está.
public interface CambiosCatalogoCatedra {

    Optional<EstadoVersiones> estadoVersiones();

    Optional<CambiosDeVersion> cambios(long version);

    Optional<CategoriaProfesional> categoria(long id);

    Optional<Profesional> profesional(long id);

    Optional<HorarioSemanal> horario(long id);
}
