package cl.duoc.msHabito.dto;

import cl.duoc.msHabito.model.Reconocimiento;

import java.time.LocalDateTime;

public record ReconocimientoResponse(
        String codigo,
        String titulo,
        String descripcion,
        LocalDateTime fechaOtorgado
) {
    public static ReconocimientoResponse desde(Reconocimiento r) {
        return new ReconocimientoResponse(r.getCodigo().name(), r.getCodigo().getTitulo(),
                r.getCodigo().getDescripcion(), r.getFechaOtorgado());
    }
}
