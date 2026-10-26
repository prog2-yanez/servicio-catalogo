package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

@Service
@Transactional(readOnly = true)
public class BuscarProfesionalesService implements BuscarProfesionalesUseCase {

    private final ConsultaProfesionalesRepository consultaProfesionales;

    public BuscarProfesionalesService(ConsultaProfesionalesRepository consultaProfesionales) {
        this.consultaProfesionales = consultaProfesionales;
    }

    // Busca solo en la copia local; si todavía no hay datos, la página viene vacía.
    @Override
    public Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido) {
        return consultaProfesionales.buscar(filtro, pedido);
    }
}
