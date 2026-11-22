package cl.duoc.msHabito.dto;

import cl.duoc.msHabito.model.TipoAviso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AvisoResponse(
        TipoAviso tipo,
        String folio,
        String empresa,
        String concepto,
        LocalDate vencimiento,
        @Schema(description = "Días que faltan para el vencimiento (negativo si ya venció)") long diasRestantes,
        Integer montoAPagar,
        String mensaje
) {
}
