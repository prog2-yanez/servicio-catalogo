package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalDeshabilitadoException;
import prog2.serviciocatalogo.catalogo.domain.exception.ProfesionalNoEncontradoException;
import prog2.serviciocatalogo.catalogo.domain.model.AgendaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.domain.model.Profesional;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaProfesionalesRepository;

class ConsultarProfesionalServiceTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final ConsultaProfesionalesRepository consulta = mock(ConsultaProfesionalesRepository.class);
    private final ConsultarProfesionalService servicio =
            new ConsultarProfesionalService(catalogoRepository, consulta);

    @Test
    void devuelveElProfesionalConSusHorariosYLaVersion() {
        conVersion(7);
        Profesional ana = profesional(true);
        HorarioSemanal lunes = new HorarioSemanal(21L, 15L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0), 30, true, CREADO, CREADO);
        when(consulta.buscarPorId(15)).thenReturn(Optional.of(ana));
        when(consulta.horariosHabilitados(15)).thenReturn(List.of(lunes));

        AgendaProfesional agenda = servicio.obtenerConAgenda(15);

        assertThat(agenda).isEqualTo(new AgendaProfesional(ana, List.of(lunes), 7));
    }

    @Test
    void unProfesionalInexistenteDaNoEncontrado() {
        conVersion(7);
        when(consulta.buscarPorId(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerConAgenda(99)).isInstanceOf(ProfesionalNoEncontradoException.class);
    }

    @Test
    void unProfesionalDeshabilitadoDaUnErrorDistinto() {
        conVersion(7);
        when(consulta.buscarPorId(15)).thenReturn(Optional.of(profesional(false)));

        assertThatThrownBy(() -> servicio.obtenerConAgenda(15))
                .isInstanceOf(ProfesionalDeshabilitadoException.class);
    }

    @Test
    void sinDatosUnProfesionalDaNoEncontrado() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        when(consulta.buscarPorId(15)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerConAgenda(15)).isInstanceOf(ProfesionalNoEncontradoException.class);
    }

    @Test
    void unProfesionalSinVersionAplicadaEsUnErrorInterno() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());
        when(consulta.buscarPorId(15)).thenReturn(Optional.of(profesional(true)));

        assertThatThrownBy(() -> servicio.obtenerConAgenda(15)).isInstanceOf(IllegalStateException.class);
    }

    private void conVersion(long numero) {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.of(new VersionCatalogo(numero, CREADO)));
    }

    private static Profesional profesional(boolean habilitado) {
        return new Profesional(15L, 1L, "Ana", "Gomez", habilitado, CREADO, CREADO);
    }
}
