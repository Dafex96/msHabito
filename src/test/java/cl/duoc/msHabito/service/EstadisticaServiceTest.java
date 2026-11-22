package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticaServiceTest {

    @Mock
    PagoHabitoRepository pagos;
    @InjectMocks
    EstadisticaService servicio;

    private List<PagoHabito> pagos(int... anticipaciones) {
        List<PagoHabito> lista = new ArrayList<>();
        LocalDate base = LocalDate.of(2026, 1, 10);
        for (int i = 0; i < anticipaciones.length; i++) {
            LocalDate vencimiento = base.plusMonths(i);
            lista.add(PagoHabito.builder().id((long) i).usuarioId(1L).folio("F" + i).empresa("Enel").concepto("Luz")
                    .monto(1000).vencimiento(vencimiento).fechaPago(vencimiento.minusDays(anticipaciones[i])).build());
        }
        return lista;
    }

    @Test
    void sinPagosTodoEnCero() {
        EstadisticaResponse e = servicio.calcular(List.of());
        assertEquals(new EstadisticaResponse(0, 0, 0, 0, 0, 0, 0), e);
    }

    @Test
    void calculaPorcentajeRachasYPromedio() {
        EstadisticaResponse e = servicio.calcular(pagos(3, -2, 5, 6, 5, 1));

        assertEquals(6, e.totalPagos());
        assertEquals(5, e.pagosPuntuales());
        assertEquals(3, e.pagosAnticipados());
        assertEquals(83.3, e.porcentajePuntualidad());
        assertEquals(4, e.rachaActual());
        assertEquals(4, e.mejorRacha());
        assertEquals(3.0, e.promedioDiasAnticipacion());
    }

    @Test
    void laMejorRachaPuedeSerAnteriorALaActual() {
        EstadisticaResponse e = servicio.calcular(pagos(1, 1, 1, -1, 1));
        assertEquals(3, e.mejorRacha());
        assertEquals(1, e.rachaActual());
    }

    @Test
    void pagarElMismoDiaDelVencimientoEsPuntual() {
        EstadisticaResponse e = servicio.calcular(pagos(0));
        assertEquals(1, e.pagosPuntuales());
        assertEquals(100.0, e.porcentajePuntualidad());
        assertEquals(0.0, e.promedioDiasAnticipacion());
    }

    @Test
    void todosConAtrasoNoTienenRacha() {
        EstadisticaResponse e = servicio.calcular(pagos(-1, -3));
        assertEquals(0, e.pagosPuntuales());
        assertEquals(0, e.rachaActual());
        assertEquals(0, e.mejorRacha());
        assertEquals(-2.0, e.promedioDiasAnticipacion());
    }

    @Test
    void deUsuarioLeeLosPagosDelRepositorio() {
        when(pagos.findByUsuarioIdOrderByVencimientoAscFechaPagoAscIdAsc(1L)).thenReturn(pagos(2, 2));
        assertEquals(2, servicio.deUsuario(1L).totalPagos());
    }
}
