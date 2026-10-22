package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.ProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.specification.ProfesionalSpecifications;

@Component
public class ConsultaProfesionalesPersistenceAdapter implements ConsultaProfesionalesRepository {

    private static final Sort ORDEN = Sort.by("apellido", "nombre", "id");

    private final JpaProfesionalRepository profesionalRepository;
    private final ProfesionalMapper profesionalMapper;

    public ConsultaProfesionalesPersistenceAdapter(
            JpaProfesionalRepository profesionalRepository, ProfesionalMapper profesionalMapper) {
        this.profesionalRepository = profesionalRepository;
        this.profesionalMapper = profesionalMapper;
    }

    @Override
    public Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido) {
        Page<ProfesionalEntity> resultado = profesionalRepository.findAll(
                ProfesionalSpecifications.segun(filtro),
                PageRequest.of(pedido.numero(), pedido.tamanio(), ORDEN));
        return new Pagina<>(resultado.getContent(), pedido.numero(), pedido.tamanio(), resultado.getTotalElements())
                .map(profesionalMapper::aDominio);
    }
}
