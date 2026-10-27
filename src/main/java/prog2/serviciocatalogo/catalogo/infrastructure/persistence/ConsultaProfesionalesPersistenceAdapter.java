package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.HorarioSemanalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.ProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.specification.ProfesionalSpecifications;

@Component
public class ConsultaProfesionalesPersistenceAdapter implements ConsultaProfesionalesRepository {

    private static final Sort ORDEN = Sort.by("apellido", "nombre", "id");

    private final JpaProfesionalRepository profesionalRepository;
    private final JpaHorarioSemanalRepository horarioRepository;
    private final ProfesionalMapper profesionalMapper;
    private final HorarioSemanalMapper horarioMapper;

    public ConsultaProfesionalesPersistenceAdapter(
            JpaProfesionalRepository profesionalRepository,
            JpaHorarioSemanalRepository horarioRepository,
            ProfesionalMapper profesionalMapper,
            HorarioSemanalMapper horarioMapper) {
        this.profesionalRepository = profesionalRepository;
        this.horarioRepository = horarioRepository;
        this.profesionalMapper = profesionalMapper;
        this.horarioMapper = horarioMapper;
    }

    @Override
    public Pagina<Profesional> buscar(FiltroProfesionales filtro, PedidoPagina pedido) {
        Page<ProfesionalEntity> resultado = profesionalRepository.findAll(
                ProfesionalSpecifications.segun(filtro),
                PageRequest.of(pedido.numero(), pedido.tamanio(), ORDEN));
        return new Pagina<>(resultado.getContent(), pedido.numero(), pedido.tamanio(), resultado.getTotalElements())
                .map(profesionalMapper::aDominio);
    }

    @Override
    public Optional<Profesional> buscarPorId(long id) {
        return profesionalRepository.findById(id).map(profesionalMapper::aDominio);
    }

    @Override
    public List<HorarioSemanal> horariosHabilitados(long profesionalId) {
        return horarioRepository.findByProfesionalIdAndHabilitadoTrue(profesionalId).stream()
                .map(horarioMapper::aDominio)
                .toList();
    }
}
