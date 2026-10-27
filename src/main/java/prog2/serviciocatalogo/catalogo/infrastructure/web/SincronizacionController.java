package prog2.serviciocatalogo.catalogo.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.serviciocatalogo.catalogo.domain.ports.in.ConsultarEstadoSincronizacionUseCase;
import prog2.serviciocatalogo.catalogo.infrastructure.web.dto.EstadoSincronizacionResponse;

@RestController
@RequestMapping("/api/v1/sincronizacion")
public class SincronizacionController {

    private final ConsultarEstadoSincronizacionUseCase consultarEstado;

    public SincronizacionController(ConsultarEstadoSincronizacionUseCase consultarEstado) {
        this.consultarEstado = consultarEstado;
    }

    @GetMapping("/estado")
    public EstadoSincronizacionResponse estado() {
        return EstadoSincronizacionResponse.desde(consultarEstado.consultar());
    }
}
