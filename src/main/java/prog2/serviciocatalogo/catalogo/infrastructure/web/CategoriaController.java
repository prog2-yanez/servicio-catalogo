package prog2.serviciocatalogo.catalogo.infrastructure.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prog2.serviciocatalogo.catalogo.domain.model.PedidoPagina;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarCategoriasUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.CategoriaResponse;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.PaginaResponse;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final ConsultarCategoriasUseCase consultarCategorias;

    public CategoriaController(ConsultarCategoriasUseCase consultarCategorias) {
        this.consultarCategorias = consultarCategorias;
    }

    @GetMapping
    public PaginaResponse<CategoriaResponse> listar(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "debe ser 0 o mayor") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "debe ser 1 o mayor")
            @Max(value = PedidoPagina.TAMANIO_MAXIMO, message = "debe ser 100 o menor") int size) {
        return PaginaResponse.desde(consultarCategorias.listar(new PedidoPagina(page, size)), CategoriaResponse::desde);
    }

    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable @Positive(message = "debe ser mayor a 0") long id) {
        return CategoriaResponse.desde(consultarCategorias.obtener(id));
    }
}
