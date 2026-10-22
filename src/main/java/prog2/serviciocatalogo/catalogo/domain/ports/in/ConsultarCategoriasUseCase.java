package prog2.serviciocatalogo.catalogo.domain.ports.in;

import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;

public interface ConsultarCategoriasUseCase {

    Pagina<CategoriaProfesional> listar(PedidoPagina pedido);

    CategoriaProfesional obtener(long id);
}
