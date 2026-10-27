package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaCategoriasRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.CategoriaProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.CategoriaProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;

@Component
public class ConsultaCategoriasPersistenceAdapter implements ConsultaCategoriasRepository {

    private static final Sort ORDEN = Sort.by("nombre", "id");

    private final JpaCategoriaProfesionalRepository categoriaRepository;
    private final CategoriaProfesionalMapper categoriaMapper;

    public ConsultaCategoriasPersistenceAdapter(
            JpaCategoriaProfesionalRepository categoriaRepository, CategoriaProfesionalMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public Pagina<CategoriaProfesional> listar(PedidoPagina pedido) {
        Page<CategoriaProfesionalEntity> resultado =
                categoriaRepository.findAll(PageRequest.of(pedido.numero(), pedido.tamanio(), ORDEN));
        return new Pagina<>(resultado.getContent(), pedido.numero(), pedido.tamanio(), resultado.getTotalElements())
                .map(categoriaMapper::aDominio);
    }

    @Override
    public Optional<CategoriaProfesional> buscarPorId(long id) {
        return categoriaRepository.findById(id).map(categoriaMapper::aDominio);
    }
}
