package cl.duoc.msHabito.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "pagos_habito", indexes = @Index(name = "idx_pagos_habito_usuario", columnList = "usuario_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoHabito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_habito")
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, unique = true, length = 40)
    private String folio;

    @Column(nullable = false, length = 120)
    private String empresa;

    @Column(nullable = false, length = 200)
    private String concepto;

    @Column(nullable = false)
    private Integer monto;

    @Column(nullable = false)
    private LocalDate vencimiento;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    public boolean puntual() {
        return !fechaPago.isAfter(vencimiento);
    }

    public long diasAnticipacion() {
        return ChronoUnit.DAYS.between(fechaPago, vencimiento);
    }
}
