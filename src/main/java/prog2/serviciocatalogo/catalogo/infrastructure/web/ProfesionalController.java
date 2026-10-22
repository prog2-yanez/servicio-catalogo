package prog2.serviciocatalogo.catalogo.infrastructure.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.in.BuscarProfesionalesUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.PaginaResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.ProfesionalResumenResponse;

@RestController
@RequestMapping("/api/v1/profesionales")
public class ProfesionalController {

    private final BuscarProfesionalesUseCase buscarProfesionales;

    public ProfesionalController(BuscarProfesionalesUseCase buscarProfesionales) {
        this.buscarProfesionales = buscarProfesionales;
    }

    @GetMapping
    public PaginaResponse<ProfesionalResumenResponse> buscar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "debe ser 0 o mayor") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "debe ser 1 o mayor")
            @Max(value = PedidoPagina.TAMANIO_MAXIMO, message = "debe ser 100 o menor") int size) {
        return PaginaResponse.desde(
                buscarProfesionales.buscar(new FiltroProfesionales(categoriaId, nombre), new PedidoPagina(page, size)),
                ProfesionalResumenResponse::desde);
    }
}
