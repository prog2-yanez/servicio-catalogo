package prog2.serviciocatalogo.catalogo.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import prog2.serviciocatalogo.TestcontainersConfiguration;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.VersionCatalogoEntity;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.CategoriaProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.HorarioSemanalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.ProfesionalMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper.VersionCatalogoMapper;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaCategoriaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaHorarioSemanalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaProfesionalRepository;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.repository.JpaVersionCatalogoRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
        CategoriaProfesionalMapper.class,
        ProfesionalMapper.class,
        HorarioSemanalMapper.class,
        VersionCatalogoMapper.class
})
class PersistenciaCatalogoTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");
    private static final Instant ACTUALIZADO = Instant.parse("2026-09-10T13:40:00Z");

    @Autowired
    private JpaCategoriaProfesionalRepository categorias;

    @Autowired
    private JpaProfesionalRepository profesionales;

    @Autowired
    private JpaHorarioSemanalRepository horarios;

    @Autowired
    private JpaVersionCatalogoRepository versiones;

    @Autowired
    private CategoriaProfesionalMapper categoriaMapper;

    @Autowired
    private ProfesionalMapper profesionalMapper;

    @Autowired
    private HorarioSemanalMapper horarioMapper;

    @Autowired
    private VersionCatalogoMapper versionMapper;

    @Test
    void guardaYRecuperaUnProfesionalConSuCategoriaYHorario() {
        CategoriaProfesional categoria = categoria(true);
        Profesional profesional = profesional(true);
        HorarioSemanal horario = new HorarioSemanal(21L, 15L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true, CREADO, ACTUALIZADO);

        categorias.saveAndFlush(categoriaMapper.aEntidad(categoria));
        profesionales.saveAndFlush(profesionalMapper.aEntidad(profesional));
        horarios.saveAndFlush(horarioMapper.aEntidad(horario));

        assertThat(categorias.findById(1L).map(categoriaMapper::aDominio)).contains(categoria);
        assertThat(profesionales.findById(15L).map(profesionalMapper::aDominio)).contains(profesional);
        assertThat(horarios.findById(21L).map(horarioMapper::aDominio)).contains(horario);
    }

    @Test
    void conservaLasEntidadesDeshabilitadas() {
        categorias.saveAndFlush(categoriaMapper.aEntidad(categoria(true)));
        profesionales.saveAndFlush(profesionalMapper.aEntidad(profesional(false)));

        assertThat(profesionales.findById(15L).map(profesionalMapper::aDominio))
                .hasValueSatisfying(guardado -> assertThat(guardado.habilitado()).isFalse());
    }

    @Test
    void guardarUnaVersionNuevaReemplazaLaAnterior() {
        versiones.saveAndFlush(versionMapper.aEntidad(new VersionCatalogo(3, CREADO)));
        versiones.saveAndFlush(versionMapper.aEntidad(new VersionCatalogo(7, ACTUALIZADO)));

        assertThat(versiones.count()).isEqualTo(1);
        assertThat(versiones.findById(VersionCatalogoEntity.ID_UNICO).map(versionMapper::aDominio))
                .contains(new VersionCatalogo(7, ACTUALIZADO));
    }

    @Test
    void rechazaUnProfesionalConCategoriaInexistente() {
        assertThatThrownBy(() -> profesionales.saveAndFlush(profesionalMapper.aEntidad(profesional(true))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static CategoriaProfesional categoria(boolean habilitada) {
        return new CategoriaProfesional(1L, "Clinica medica", "Atencion clinica general",
                habilitada, CREADO, ACTUALIZADO);
    }

    private static Profesional profesional(boolean habilitado) {
        return new Profesional(15L, 1L, "Ana", "Gomez", habilitado, CREADO, ACTUALIZADO);
    }
}
