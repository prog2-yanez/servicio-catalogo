package prog2.serviciocatalogo.catalogo.domain.ports.out;

import java.util.Optional;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;

public interface ConsultaCategoriasRepository {

    Pagina<CategoriaProfesional> listar(PedidoPagina pedido);

    Optional<CategoriaProfesional> buscarPorId(long id);
}
