package cl.duoc.msHabito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Preferencias de aviso de vencimiento")
public record PreferenciaRequest(
        @Schema(example = "true") @NotNull Boolean avisosActivos,
        @Schema(description = "Días de anticipación con que se avisa (1 a 15)", example = "3")
        @NotNull @Min(1) @Max(15) Integer diasAnticipacion
) {
}
