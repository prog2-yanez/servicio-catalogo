package prog2.serviciocatalogo.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prog2.serviciocatalogo.catalogo.domain.exception.CatalogoSinDatosException;
import prog2.serviciocatalogo.catalogo.domain.exception.CategoriaNoEncontradaException;
import prog2.serviciocatalogo.catalogo.domain.model.CategoriaProfesional;
import prog2.serviciocatalogo.catalogo.domain.model.Pagina;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.model.VersionCatalogo;
import prog2.serviciocatalogo.catalogo.domain.ports.out.CatalogoRepository;
import prog2.serviciocatalogo.catalogo.domain.ports.out.ConsultaCategoriasRepository;

class ConsultarCategoriasServiceTests {

    private static final Instant CREADO = Instant.parse("2026-07-01T12:00:00Z");
    private static final CategoriaProfesional CLINICA =
            new CategoriaProfesional(1L, "Clinica medica", "Atencion general", true, CREADO, CREADO);

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final ConsultaCategoriasRepository consulta = mock(ConsultaCategoriasRepository.class);
    private final ConsultarCategoriasService servicio = new ConsultarCategoriasService(new GuardiaCatalogo(catalogoRepository), consulta);

    @BeforeEach
    void conDatos() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.of(new VersionCatalogo(7, CREADO)));
    }

    @Test
    void listaLasCategoriasPaginadas() {
        PedidoPagina pedido = new PedidoPagina(0, 20);
        Pagina<CategoriaProfesional> pagina = new Pagina<>(List.of(CLINICA), 0, 20, 1);
        when(consulta.listar(pedido)).thenReturn(pagina);

        assertThat(servicio.listar(pedido)).isSameAs(pagina);
    }

    @Test
    void devuelveUnaCategoriaExistente() {
        when(consulta.buscarPorId(1)).thenReturn(Optional.of(CLINICA));

        assertThat(servicio.obtener(1)).isEqualTo(CLINICA);
    }

    @Test
    void unaCategoriaInexistenteDaNoEncontrada() {
        when(consulta.buscarPorId(9)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(9))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessageContaining("9");
    }

    @Test
    void sinDatosAvisaQueElCatalogoNoEstaListo() {
        when(catalogoRepository.obtenerVersion()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.listar(new PedidoPagina(0, 20)))
                .isInstanceOf(CatalogoSinDatosException.class);
        assertThatThrownBy(() -> servicio.obtener(1)).isInstanceOf(CatalogoSinDatosException.class);
        verifyNoInteractions(consulta);
    }
}
