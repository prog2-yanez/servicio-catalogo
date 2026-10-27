package prog2.serviciocatalogo.catalogo.infrastructure.persistence.specification;

import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import prog2.serviciocatalogo.catalogo.domain.model.FiltroProfesionales;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.ProfesionalEntity;

// Cada filtro es una condición que se suma solo si viene en el pedido.
public final class ProfesionalSpecifications {

    private static final char ESCAPE = '\\';

    private ProfesionalSpecifications() {
    }

    public static Specification<ProfesionalEntity> segun(FiltroProfesionales filtro) {
        Specification<ProfesionalEntity> condicion = Specification.unrestricted();
        if (filtro.filtraPorCategoria()) {
            condicion = condicion.and(deCategoria(filtro.categoriaId()));
        }
        if (filtro.filtraPorNombre()) {
            condicion = condicion.and(conNombre(filtro.nombre()));
        }
        if (filtro.filtraPorHabilitado()) {
            condicion = condicion.and(estaHabilitado(filtro.habilitado()));
        }
        return condicion;
    }

    static Specification<ProfesionalEntity> deCategoria(long categoriaId) {
        return (raiz, consulta, cb) -> cb.equal(raiz.get("categoriaId"), categoriaId);
    }

    static Specification<ProfesionalEntity> estaHabilitado(boolean habilitado) {
        return (raiz, consulta, cb) -> cb.equal(raiz.get("habilitado"), habilitado);
    }

    // Busca el texto dentro del nombre o del apellido, sin distinguir mayúsculas.
    static Specification<ProfesionalEntity> conNombre(String texto) {
        String patron = "%" + escaparComodines(texto.toLowerCase(Locale.ROOT)) + "%";
        return (raiz, consulta, cb) -> cb.or(
                cb.like(cb.lower(raiz.get("nombre")), patron, ESCAPE),
                cb.like(cb.lower(raiz.get("apellido")), patron, ESCAPE));
    }

    // Sin esto, un % o un _ escrito por el usuario funcionaría como comodín de SQL.
    private static String escaparComodines(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
