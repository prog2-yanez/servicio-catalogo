package prog2.serviciocatalogo.catalogo.infrastructure.startup;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.ports.in.InicializarCatalogoUseCase;

@Component
public class InicializacionCatalogoListener {

    private final InicializarCatalogoUseCase inicializarCatalogo;

    public InicializacionCatalogoListener(InicializarCatalogoUseCase inicializarCatalogo) {
        this.inicializarCatalogo = inicializarCatalogo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void alArrancar() {
        inicializarCatalogo.inicializarSiEstaVacio();
    }
}
