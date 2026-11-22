package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.PagoHabitoRequest;
import cl.duoc.msHabito.dto.PagoHabitoResponse;
import cl.duoc.msHabito.dto.RegistroPagoResponse;
import cl.duoc.msHabito.exception.ReglaNegocioException;
import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.model.Reconocimiento;
import cl.duoc.msHabito.model.TipoReconocimiento;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoHabitoServiceTest {

    @Mock
    PagoHabitoRepository pagos;
    @Mock
    ReconocimientoService reconocimientos;
    @InjectMocks
    PagoHabitoService servicio;

    private final PagoHabitoRequest req = new PagoHabitoRequest(1L, " PF-1 ", " Enel ", " Luz ", 28000,
            LocalDate.of(2026, 11, 25), LocalDate.of(2026, 11, 20));

    @Test
    void registraElPagoYEntregaLosReconocimientosNuevos() {
        when(pagos.existsByFolio("PF-1")).thenReturn(false);
        when(pagos.save(any(PagoHabito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reconocimientos.evaluar(1L)).thenReturn(List.of(Reconocimiento.builder().usuarioId(1L)
                .codigo(TipoReconocimiento.PRIMER_PAGO_PUNTUAL).fechaOtorgado(LocalDateTime.of(2026, 11, 20, 9, 0)).build()));

        RegistroPagoResponse r = servicio.registrar(req);

        assertEquals("PF-1", r.pago().folio());
        assertEquals("Enel", r.pago().empresa());
        assertTrue(r.pago().puntual());
        assertEquals(5, r.pago().diasAnticipacion());
        assertEquals(1, r.reconocimientosNuevos().size());
        assertEquals("PRIMER_PAGO_PUNTUAL", r.reconocimientosNuevos().get(0).codigo());
    }

    @Test
    void unFolioYaRegistradoEsConflicto() {
        when(pagos.existsByFolio("PF-1")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(req));
        verify(pagos, never()).save(any());
        verifyNoInteractions(reconocimientos);
    }

    @Test
    void listarDevuelveElHistorial() {
        when(pagos.findByUsuarioIdOrderByFechaPagoDescIdDesc(1L)).thenReturn(List.of(
                PagoHabito.builder().usuarioId(1L).folio("PF-1").empresa("Enel").concepto("Luz").monto(1000)
                        .vencimiento(LocalDate.of(2026, 11, 25)).fechaPago(LocalDate.of(2026, 11, 27)).build()));

        List<PagoHabitoResponse> lista = servicio.listar(1L);

        assertEquals(1, lista.size());
        assertFalse(lista.get(0).puntual());
        assertEquals(-2, lista.get(0).diasAnticipacion());
    }
}
