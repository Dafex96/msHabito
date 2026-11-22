package cl.duoc.msHabito.dto;

import cl.duoc.msHabito.model.PagoHabito;

import java.time.LocalDate;

public record PagoHabitoResponse(
        Long usuarioId,
        String folio,
        String empresa,
        String concepto,
        Integer monto,
        LocalDate vencimiento,
        LocalDate fechaPago,
        boolean puntual,
        long diasAnticipacion
) {
    public static PagoHabitoResponse desde(PagoHabito p) {
        return new PagoHabitoResponse(p.getUsuarioId(), p.getFolio(), p.getEmpresa(), p.getConcepto(),
                p.getMonto(), p.getVencimiento(), p.getFechaPago(), p.puntual(), p.diasAnticipacion());
    }
}
