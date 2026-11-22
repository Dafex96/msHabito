package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    static final int DIAS_ANTICIPADO = 5;

    private final PagoHabitoRepository pagos;

    @Transactional(readOnly = true)
    public EstadisticaResponse deUsuario(Long usuarioId) {
        return calcular(pagos.findByUsuarioIdOrderByVencimientoAscFechaPagoAscIdAsc(usuarioId));
    }

    EstadisticaResponse calcular(List<PagoHabito> ordenados) {
        int total = ordenados.size();
        int puntuales = (int) ordenados.stream().filter(PagoHabito::puntual).count();
        int anticipados = (int) ordenados.stream().filter(p -> p.diasAnticipacion() >= DIAS_ANTICIPADO).count();

        int actual = 0;
        int mejor = 0;
        for (PagoHabito p : ordenados) {
            if (p.puntual()) {
                actual++;
                mejor = Math.max(mejor, actual);
            } else {
                actual = 0;
            }
        }

        double porcentaje = total == 0 ? 0 : redondear(puntuales * 100.0 / total);
        double promedio = redondear(ordenados.stream().mapToLong(PagoHabito::diasAnticipacion).average().orElse(0));
        return new EstadisticaResponse(total, puntuales, anticipados, porcentaje, actual, mejor, promedio);
    }

    private static double redondear(double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}
