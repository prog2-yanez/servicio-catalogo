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

    private final GuardiaCatalogo guardiaCatalogo;
    private final ConsultaCategoriasRepository consultaCategorias;

    public ConsultarCategoriasService(
            GuardiaCatalogo guardiaCatalogo, ConsultaCategoriasRepository consultaCategorias) {
        this.guardiaCatalogo = guardiaCatalogo;
        this.consultaCategorias = consultaCategorias;
    }

    @Override
    public Pagina<CategoriaProfesional> listar(PedidoPagina pedido) {
        guardiaCatalogo.exigirDatos();
        return consultaCategorias.listar(pedido);
    }

    @Override
    public CategoriaProfesional obtener(long id) {
        guardiaCatalogo.exigirDatos();
        return consultaCategorias.buscarPorId(id).orElseThrow(() -> new CategoriaNoEncontradaException(id));
    }
}
