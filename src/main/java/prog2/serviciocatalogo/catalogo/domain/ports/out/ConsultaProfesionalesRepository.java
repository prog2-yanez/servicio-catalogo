package prog2.serviciocatalogo.catalogo.domain.ports.out;

import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;

public interface ConsultaProfesionalesRepository {

    Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido);
}
