package prog2.serviciocatalogo.catalogo.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

@Service
@Transactional(readOnly = true)
public class BuscarProfesionalesService implements BuscarProfesionalesUseCase {

    private final CatalogoRepository catalogoRepository;
    private final ConsultaProfesionalesRepository consultaProfesionales;

    public BuscarProfesionalesService(
            CatalogoRepository catalogoRepository, ConsultaProfesionalesRepository consultaProfesionales) {
        this.catalogoRepository = catalogoRepository;
        this.consultaProfesionales = consultaProfesionales;
    }

    // Busca solo en la copia local; si todavía no hay datos, no responde una lista vacía engañosa.
    @Override
    public Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido) {
        if (catalogoRepository.obtenerVersion().isEmpty()) {
            throw new CatalogoSinDatosException();
        }
        return consultaProfesionales.buscar(filtro, pedido);
    }
}
