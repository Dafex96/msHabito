package cl.duoc.msHabito.model;

import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.dto.PagoHabitoResponse;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.dto.ReconocimientoResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModeloTest {

    private PagoHabito pago(int diasAnticipacion) {
        LocalDate vencimiento = LocalDate.of(2026, 11, 25);
        return PagoHabito.builder().usuarioId(1L).folio("PF-1").empresa("Enel").concepto("Luz").monto(1000)
                .vencimiento(vencimiento).fechaPago(vencimiento.minusDays(diasAnticipacion)).build();
    }

    @Test
    void puntualidadYDiasDeAnticipacion() {
        assertTrue(pago(3).puntual());
        assertEquals(3, pago(3).diasAnticipacion());
        assertTrue(pago(0).puntual());
        assertFalse(pago(-1).puntual());
        assertEquals(-1, pago(-1).diasAnticipacion());
    }

    @Test
    void lasCondicionesDeLosReconocimientos() {
        EstadisticaResponse vacia = new EstadisticaResponse(0, 0, 0, 0, 0, 0, 0);
        EstadisticaResponse perfecta = new EstadisticaResponse(6, 6, 3, 100, 6, 6, 5);
        EstadisticaResponse cuatroDeCinco = new EstadisticaResponse(5, 4, 0, 80, 4, 4, 1);

        for (TipoReconocimiento t : TipoReconocimiento.values()) {
            assertFalse(t.cumple(vacia), t.name());
            assertTrue(t.cumple(perfecta), t.name());
            assertNotNull(t.getTitulo());
            assertNotNull(t.getDescripcion());
        }
        assertFalse(TipoReconocimiento.PUNTUALIDAD_IMPECABLE.cumple(cuatroDeCinco));
        assertTrue(TipoReconocimiento.RACHA_3.cumple(cuatroDeCinco));
        assertFalse(TipoReconocimiento.RACHA_6.cumple(cuatroDeCinco));
    }

    @Test
    void preferenciaPorDefecto() {
        PreferenciaAviso p = PreferenciaAviso.builder().usuarioId(1L).build();
        assertTrue(p.getAvisosActivos());
        assertEquals(3, p.getDiasAnticipacion());
    }

    @Test
    void dtosMapeanLasEntidades() {
        PagoHabitoResponse pr = PagoHabitoResponse.desde(pago(2));
        assertEquals("PF-1", pr.folio());
        assertTrue(pr.puntual());
        assertEquals(2, pr.diasAnticipacion());

        Reconocimiento r = Reconocimiento.builder().usuarioId(1L).codigo(TipoReconocimiento.RACHA_6)
                .fechaOtorgado(LocalDateTime.of(2026, 11, 22, 10, 0)).build();
        ReconocimientoResponse rr = ReconocimientoResponse.desde(r);
        assertEquals("RACHA_6", rr.codigo());
        assertEquals("Racha de 6", rr.titulo());

        PreferenciaResponse prf = PreferenciaResponse.desde(
                PreferenciaAviso.builder().usuarioId(1L).avisosActivos(false).diasAnticipacion(9).build());
        assertFalse(prf.avisosActivos());
        assertEquals(9, prf.diasAnticipacion());
    }
}
