package cl.duoc.msHabito.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompromisoCobranza(
        String folio,
        String empresa,
        String concepto,
        Integer monto,
        LocalDate vencimiento,
        String estado,
        Integer montoAPagar
) {
}
