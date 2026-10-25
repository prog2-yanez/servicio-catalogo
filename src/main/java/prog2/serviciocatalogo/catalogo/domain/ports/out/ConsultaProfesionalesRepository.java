package prog2.serviciocatalogo.catalogo.domain.ports.out;

import java.util.List;
import java.util.Optional;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;

public interface ConsultaProfesionalesRepository {

    Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido);

    Optional<Profesional> buscarPorId(long id);

    List<HorarioSemanal> horariosHabilitados(long profesionalId);
}
