package cl.duoc.msHabito.dto;

import java.util.List;

public record RegistroPagoResponse(
        PagoHabitoResponse pago,
        List<ReconocimientoResponse> reconocimientosNuevos
) {
}
