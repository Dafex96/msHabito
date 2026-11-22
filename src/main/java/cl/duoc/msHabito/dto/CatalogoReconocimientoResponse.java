package cl.duoc.msHabito.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CatalogoReconocimientoResponse(
        String codigo,
        String titulo,
        String descripcion,
        @Schema(description = "Si el usuario ya obtuvo este reconocimiento") boolean obtenido
) {
}
