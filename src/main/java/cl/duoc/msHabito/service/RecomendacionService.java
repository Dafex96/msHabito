package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.dto.RecomendacionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecomendacionService {

    private final EstadisticaService estadisticas;
    private final PreferenciaService preferencias;

    public List<RecomendacionResponse> recomendacionesDe(Long usuarioId) {
        EstadisticaResponse e = estadisticas.deUsuario(usuarioId);
        PreferenciaResponse p = preferencias.obtener(usuarioId);
        List<RecomendacionResponse> lista = new ArrayList<>();

        if (!p.avisosActivos()) {
            lista.add(new RecomendacionResponse("AVISOS", "Activa los avisos",
                    "Con los avisos activados te recordamos cada vencimiento antes de que sea tarde."));
        }
        if (e.totalPagos() == 0) {
            lista.add(new RecomendacionResponse("INICIO", "Aún no tienes historial",
                    "Paga tus compromisos a tiempo para ganar reconocimientos y recibir recomendaciones personalizadas."));
            return lista;
        }

        long porcentaje = Math.round(e.porcentajePuntualidad());
        if (e.porcentajePuntualidad() < 70) {
            String mensaje = p.diasAnticipacion() < 5
                    ? "Pagas a tiempo el " + porcentaje + "% de tus compromisos. Sube tus avisos a 5 días de anticipación para pagar sin apuro."
                    : "Pagas a tiempo el " + porcentaje + "% de tus compromisos. Intenta pagar apenas recibas el aviso, sin esperar al último día.";
            lista.add(new RecomendacionResponse("ANTICIPACION", "Anticipa tus pagos", mensaje));
        } else if (e.porcentajePuntualidad() >= 90) {
            lista.add(new RecomendacionResponse("FELICITACION", "¡Excelente hábito!",
                    "Pagas a tiempo el " + porcentaje + "% de tus compromisos. Sigue así para desbloquear más reconocimientos."));
        }

        if (e.rachaActual() == 0) {
            lista.add(new RecomendacionResponse("RACHA", "Retoma tu racha",
                    "Tu último pago fue después del vencimiento. Un pago puntual inicia una nueva racha."));
        } else if (e.rachaActual() >= 3) {
            lista.add(new RecomendacionResponse("RACHA", "Mantén tu racha",
                    "Llevas " + e.rachaActual() + " pagos puntuales seguidos. ¡No la cortes!"));
        }

        if (e.promedioDiasAnticipacion() < 1 && e.porcentajePuntualidad() >= 70) {
            lista.add(new RecomendacionResponse("MARGEN", "Paga con más margen",
                    "En promedio pagas el mismo día del vencimiento. Pagar 2 o 3 días antes evita imprevistos."));
        }
        return lista;
    }
}
