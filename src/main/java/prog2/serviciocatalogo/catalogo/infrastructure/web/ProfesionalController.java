package prog2.serviciocatalogo.catalogo.infrastructure.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarProfesionalUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.PaginaResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.ProfesionalConAgendaResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.ProfesionalResumenResponse;

@RestController
@RequestMapping("/api/v1/profesionales")
public class ProfesionalController {

    private final BuscarProfesionalesUseCase buscarProfesionales;
    private final ConsultarProfesionalUseCase consultarProfesional;

    public ProfesionalController(
            BuscarProfesionalesUseCase buscarProfesionales, ConsultarProfesionalUseCase consultarProfesional) {
        this.buscarProfesionales = buscarProfesionales;
        this.consultarProfesional = consultarProfesional;
    }

    @GetMapping
    public PaginaResponse<ProfesionalResumenResponse> buscar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean habilitado,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "debe ser 0 o mayor") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "debe ser 1 o mayor")
            @Max(value = PedidoPagina.TAMANIO_MAXIMO, message = "debe ser 100 o menor") int size) {
        FiltroProfesionales filtro = new FiltroProfesionales(categoriaId, nombre, habilitado);
        return PaginaResponse.desde(
                buscarProfesionales.buscar(filtro, new PedidoPagina(page, size)), ProfesionalResumenResponse::desde);
    }

    // Lo usa Turnos antes de dar un turno: datos vigentes, horarios habilitados y versión del catálogo.
    @GetMapping("/{id}")
    public ProfesionalConAgendaResponse obtener(@PathVariable long id) {
        return ProfesionalConAgendaResponse.desde(consultarProfesional.obtenerConAgenda(id));
    }
}
