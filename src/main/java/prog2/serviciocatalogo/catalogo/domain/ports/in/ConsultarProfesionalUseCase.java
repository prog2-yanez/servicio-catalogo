package prog2.serviciocatalogo.catalogo.domain.ports.in;

import prog2.serviciocatalogo.catalogo.domain.model.AgendaProfesional;

public interface ConsultarProfesionalUseCase {

    AgendaProfesional obtenerConAgenda(long id);
}
