package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.exception.CategoriaNoEncontradaException;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarCategoriasUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaCategoriasRepository;

@Service
@Transactional(readOnly = true)
public class ConsultarCategoriasService implements ConsultarCategoriasUseCase {

    private final ConsultaCategoriasRepository consultaCategorias;

    public ConsultarCategoriasService(ConsultaCategoriasRepository consultaCategorias) {
        this.consultaCategorias = consultaCategorias;
    }

    @Override
    public Pagina<CategoriaProfesional> listar(PedidoPagina pedido) {
        return consultaCategorias.listar(pedido);
    }

    @Override
    public CategoriaProfesional obtener(long id) {
        return consultaCategorias.buscarPorId(id).orElseThrow(() -> new CategoriaNoEncontradaException(id));
    }
}
