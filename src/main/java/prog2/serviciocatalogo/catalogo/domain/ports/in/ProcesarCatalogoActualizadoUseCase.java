package prog2.serviciocatalogo.catalogo.domain.ports.in;

import prog2.serviciocatalogo.catalogo.domain.model.AvisoCatalogoActualizado;

public interface ProcesarCatalogoActualizadoUseCase {

    // Devuelve false si el aviso ya se había procesado y se descartó.
    boolean procesar(AvisoCatalogoActualizado aviso);
}
