package cl.duoc.msHabito.dto;

import cl.duoc.msHabito.model.PreferenciaAviso;

public record PreferenciaResponse(
        Long usuarioId,
        boolean avisosActivos,
        int diasAnticipacion
) {
    public static PreferenciaResponse desde(PreferenciaAviso p) {
        return new PreferenciaResponse(p.getUsuarioId(), p.getAvisosActivos(), p.getDiasAnticipacion());
    }
}
