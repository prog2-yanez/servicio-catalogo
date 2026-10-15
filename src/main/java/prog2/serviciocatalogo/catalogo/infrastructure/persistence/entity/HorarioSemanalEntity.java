package prog2.serviciocatalogo.catalogo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "horario_semanal")
@Getter
@Setter
@NoArgsConstructor
public class HorarioSemanalEntity {

    @Id
    private Long id;

    @Column(name = "profesional_id", nullable = false)
    private Long profesionalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 9)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "duracion_turno_minutos", nullable = false)
    private int duracionTurnoMinutos;

    @Column(nullable = false)
    private boolean habilitado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}
