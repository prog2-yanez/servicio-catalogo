package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import java.util.Optional;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.SnapshotCatalogo;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.CategoriaProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.HorarioSemanalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.ProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.VersionCatalogoMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

@Component
public class CatalogoPersistenceAdapter implements CatalogoRepository {

    private final JpaCategoriaProfesionalRepository categoriaRepository;
    private final JpaProfesionalRepository profesionalRepository;
    private final JpaHorarioSemanalRepository horarioRepository;
    private final JpaVersionCatalogoRepository versionRepository;
    private final CategoriaProfesionalMapper categoriaMapper;
    private final ProfesionalMapper profesionalMapper;
    private final HorarioSemanalMapper horarioMapper;
    private final VersionCatalogoMapper versionMapper;

    public CatalogoPersistenceAdapter(
            JpaCategoriaProfesionalRepository categoriaRepository,
            JpaProfesionalRepository profesionalRepository,
            JpaHorarioSemanalRepository horarioRepository,
            JpaVersionCatalogoRepository versionRepository,
            CategoriaProfesionalMapper categoriaMapper,
            ProfesionalMapper profesionalMapper,
            HorarioSemanalMapper horarioMapper,
            VersionCatalogoMapper versionMapper) {
        this.categoriaRepository = categoriaRepository;
        this.profesionalRepository = profesionalRepository;
        this.horarioRepository = horarioRepository;
        this.versionRepository = versionRepository;
        this.categoriaMapper = categoriaMapper;
        this.profesionalMapper = profesionalMapper;
        this.horarioMapper = horarioMapper;
        this.versionMapper = versionMapper;
    }

    @Override
    public Optional<VersionCatalogo> obtenerVersion() {
        return versionRepository.findById(VersionCatalogoEntity.ID_UNICO).map(versionMapper::aDominio);
    }

    // Se borra de las tablas hijas a la padre y se inserta al revés para respetar las claves foráneas.
    @Override
    public void reemplazarCatalogo(SnapshotCatalogo snapshot) {
        horarioRepository.deleteAllInBatch();
        profesionalRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();

        categoriaRepository.saveAll(snapshot.categorias().stream().map(categoriaMapper::aEntidad).toList());
        profesionalRepository.saveAll(snapshot.profesionales().stream().map(profesionalMapper::aEntidad).toList());
        horarioRepository.saveAll(snapshot.horarios().stream().map(horarioMapper::aEntidad).toList());
    }

    @Override
    public void guardarVersion(VersionCatalogo version) {
        versionRepository.save(versionMapper.aEntidad(version));
    }
}
