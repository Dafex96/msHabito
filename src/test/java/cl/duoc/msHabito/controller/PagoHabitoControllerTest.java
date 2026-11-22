package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.*;
import cl.duoc.msHabito.service.EstadisticaService;
import cl.duoc.msHabito.service.PagoHabitoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoHabitoControllerTest {

    @Mock
    PagoHabitoService pagos;
    @Mock
    EstadisticaService estadisticas;
    @InjectMocks
    PagoHabitoController controller;

    private final PagoHabitoResponse pago = new PagoHabitoResponse(1L, "PF-1", "Enel", "Luz", 1000,
            LocalDate.of(2026, 11, 25), LocalDate.of(2026, 11, 20), true, 5);

    @Test
    void registrarResponde201() {
        PagoHabitoRequest req = new PagoHabitoRequest(1L, "PF-1", "Enel", "Luz", 1000,
                LocalDate.of(2026, 11, 25), LocalDate.of(2026, 11, 20));
        when(pagos.registrar(req)).thenReturn(new RegistroPagoResponse(pago, List.of()));

        ResponseEntity<RegistroPagoResponse> r = controller.registrar(req);

        assertEquals(201, r.getStatusCode().value());
        assertEquals("PF-1", r.getBody().pago().folio());
    }

    @Test
    void listarDelegaEnElServicio() {
        when(pagos.listar(1L)).thenReturn(List.of(pago));
        assertEquals(1, controller.listar(1L).size());
    }

    @Test
    void estadisticasDelegaEnElServicio() {
        EstadisticaResponse e = new EstadisticaResponse(1, 1, 1, 100, 1, 1, 5);
        when(estadisticas.deUsuario(1L)).thenReturn(e);
        assertEquals(e, controller.estadisticas(1L));
    }
}
