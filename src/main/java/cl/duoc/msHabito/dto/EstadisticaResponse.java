package cl.duoc.msHabito.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen del hábito de pago de un usuario")
public record EstadisticaResponse(
        int totalPagos,
        int pagosPuntuales,
        @Schema(description = "Pagos hechos con 5 o más días de anticipación") int pagosAnticipados,
        @Schema(description = "Porcentaje de pagos puntuales (0 a 100)") double porcentajePuntualidad,
        @Schema(description = "Pagos puntuales seguidos hasta el último pago") int rachaActual,
        int mejorRacha,
        @Schema(description = "Promedio de días de anticipación (negativo si se paga con atraso)")
        double promedioDiasAnticipacion
) {
}
