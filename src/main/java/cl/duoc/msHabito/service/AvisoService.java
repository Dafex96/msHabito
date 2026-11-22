package cl.duoc.msHabito.service;

import cl.duoc.msHabito.client.CobranzaClient;
import cl.duoc.msHabito.dto.AvisoResponse;
import cl.duoc.msHabito.dto.CompromisoCobranza;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.model.TipoAviso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final CobranzaClient cobranza;
    private final PreferenciaService preferencias;
    private final Clock clock;

    public List<AvisoResponse> avisosDe(Long usuarioId) {
        PreferenciaResponse preferencia = preferencias.obtener(usuarioId);
        if (!preferencia.avisosActivos()) {
            return List.of();
        }
        LocalDate hoy = LocalDate.now(clock);
        return cobranza.compromisosDeUsuario(usuarioId).stream()
                .filter(c -> c.vencimiento() != null && esAbierto(c.estado()))
                .map(c -> aviso(c, hoy, preferencia.diasAnticipacion()))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(AvisoResponse::vencimiento))
                .toList();
    }

    private boolean esAbierto(String estado) {
        return "Pendiente".equalsIgnoreCase(estado) || "Vencido".equalsIgnoreCase(estado);
    }

    private Optional<AvisoResponse> aviso(CompromisoCobranza c, LocalDate hoy, int diasAnticipacion) {
        long dias = ChronoUnit.DAYS.between(hoy, c.vencimiento());
        String detalle = c.concepto() + " (" + c.empresa() + ")";

        if (dias < 0) {
            return Optional.of(crear(TipoAviso.VENCIDO, c, dias,
                    "Tu pago de " + detalle + " venció hace " + texto(-dias)
                            + ". Págalo cuanto antes para evitar más recargos."));
        }
        if (dias == 0) {
            return Optional.of(crear(TipoAviso.VENCE_HOY, c, dias,
                    "Hoy vence tu pago de " + detalle + "."));
        }
        if (dias <= diasAnticipacion) {
            return Optional.of(crear(TipoAviso.PROXIMO_VENCIMIENTO, c, dias,
                    "Tu pago de " + detalle + " vence en " + texto(dias) + "."));
        }
        return Optional.empty();
    }

    private AvisoResponse crear(TipoAviso tipo, CompromisoCobranza c, long dias, String mensaje) {
        return new AvisoResponse(tipo, c.folio(), c.empresa(), c.concepto(), c.vencimiento(), dias,
                c.montoAPagar(), mensaje);
    }

    private String texto(long dias) {
        return dias == 1 ? "1 día" : dias + " días";
    }
}
