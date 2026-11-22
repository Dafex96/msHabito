package cl.duoc.msHabito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Pago realizado por un usuario, para registrar su hábito de pago")
public record PagoHabitoRequest(
        @Schema(description = "Id del usuario en el microservicio de Usuarios", example = "1")
        @NotNull @Positive Long usuarioId,
        @Schema(description = "Folio del compromiso pagado", example = "PF-LUZ-001")
        @NotBlank @Size(max = 40) String folio,
        @Schema(example = "Enel") @NotBlank @Size(max = 120) String empresa,
        @Schema(example = "Luz") @NotBlank @Size(max = 200) String concepto,
        @Schema(example = "28000") @NotNull @Positive Integer monto,
        @Schema(description = "Fecha de vencimiento del compromiso", example = "2026-11-25")
        @NotNull LocalDate vencimiento,
        @Schema(description = "Fecha en que se pagó", example = "2026-11-20")
        @NotNull LocalDate fechaPago
) {
}
