package prog2.serviciocatalogo.catalogo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import prog2.serviciocatalogo.catalogo.domain.model.HorarioSemanal;
import prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity.HorarioSemanalEntity;

@Component
public class HorarioSemanalMapper {

    public HorarioSemanalEntity aEntidad(HorarioSemanal horario) {
        HorarioSemanalEntity entidad = new HorarioSemanalEntity();
        entidad.setId(horario.id());
        entidad.setProfesionalId(horario.profesionalId());
        entidad.setDiaSemana(horario.diaSemana());
        entidad.setHoraInicio(horario.horaInicio());
        entidad.setHoraFin(horario.horaFin());
        entidad.setDuracionTurnoMinutos(horario.duracionTurnoMinutos());
        entidad.setHabilitado(horario.habilitado());
        entidad.setCreadoEn(horario.creadoEn());
        entidad.setActualizadoEn(horario.actualizadoEn());
        return entidad;
    }

    public HorarioSemanal aDominio(HorarioSemanalEntity entidad) {
        return new HorarioSemanal(
                entidad.getId(),
                entidad.getProfesionalId(),
                entidad.getDiaSemana(),
                entidad.getHoraInicio(),
                entidad.getHoraFin(),
                entidad.getDuracionTurnoMinutos(),
                entidad.isHabilitado(),
                entidad.getCreadoEn(),
                entidad.getActualizadoEn());
    }
}
