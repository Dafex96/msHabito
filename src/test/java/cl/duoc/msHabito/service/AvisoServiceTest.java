package cl.duoc.msHabito.service;

import cl.duoc.msHabito.client.CobranzaClient;
import cl.duoc.msHabito.dto.AvisoResponse;
import cl.duoc.msHabito.dto.CompromisoCobranza;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.model.TipoAviso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AvisoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 22);

    @Mock
    CobranzaClient cobranza;
    @Mock
    PreferenciaService preferencias;

    AvisoService servicio;

    @BeforeEach
    void setUp() {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-22T15:00:00Z"), ZoneId.of("America/Santiago"));
        servicio = new AvisoService(cobranza, preferencias, reloj);
        when(preferencias.obtener(1L)).thenReturn(new PreferenciaResponse(1L, true, 3));
    }

    private CompromisoCobranza cc(String folio, String estado, LocalDate vencimiento) {
        return new CompromisoCobranza(folio, "Enel", "Luz", 10000, vencimiento, estado, 10500);
    }

    @Test
    void sinAvisosActivosNoConsultaCobranza() {
        when(preferencias.obtener(1L)).thenReturn(new PreferenciaResponse(1L, false, 3));

        assertTrue(servicio.avisosDe(1L).isEmpty());
        verifyNoInteractions(cobranza);
    }

    @Test
    void generaAvisosSegunLaFechaDeVencimiento() {
        when(cobranza.compromisosDeUsuario(1L)).thenReturn(List.of(
                cc("LEJANO", "Pendiente", HOY.plusDays(4)),
                cc("PROXIMO", "Pendiente", HOY.plusDays(3)),
                cc("HOY", "Pendiente", HOY),
                cc("VENCIDO", "Vencido", HOY.minusDays(2)),
                cc("MANANA", "Pendiente", HOY.plusDays(1))));

        List<AvisoResponse> avisos = servicio.avisosDe(1L);

        assertEquals(List.of("VENCIDO", "HOY", "MANANA", "PROXIMO"), avisos.stream().map(AvisoResponse::folio).toList());
        assertEquals(TipoAviso.VENCIDO, avisos.get(0).tipo());
        assertEquals(-2, avisos.get(0).diasRestantes());
        assertTrue(avisos.get(0).mensaje().contains("venció hace 2 días"));
        assertEquals(TipoAviso.VENCE_HOY, avisos.get(1).tipo());
        assertEquals(TipoAviso.PROXIMO_VENCIMIENTO, avisos.get(2).tipo());
        assertTrue(avisos.get(2).mensaje().contains("vence en 1 día."));
        assertTrue(avisos.get(3).mensaje().contains("vence en 3 días"));
        assertEquals(10500, avisos.get(0).montoAPagar());
    }

    @Test
    void unVencimientoDeAyerUsaSingular() {
        when(cobranza.compromisosDeUsuario(1L)).thenReturn(List.of(cc("A", "vencido", HOY.minusDays(1))));
        assertTrue(servicio.avisosDe(1L).get(0).mensaje().contains("venció hace 1 día."));
    }

    @Test
    void ignoraLosCompromisosQueNoEstanAbiertos() {
        when(cobranza.compromisosDeUsuario(1L)).thenReturn(List.of(
                cc("PAGADO", "Pagado", HOY),
                cc("REVISION", "En revisión", HOY),
                cc("ANULADO", "Anulado", HOY),
                cc("SIN_ESTADO", null, HOY),
                cc("SIN_FECHA", "Pendiente", null)));

        assertTrue(servicio.avisosDe(1L).isEmpty());
    }

    @Test
    void respetaLosDiasDeAnticipacionDelUsuario() {
        when(preferencias.obtener(1L)).thenReturn(new PreferenciaResponse(1L, true, 7));
        when(cobranza.compromisosDeUsuario(1L)).thenReturn(List.of(cc("A", "Pendiente", HOY.plusDays(7))));

        assertEquals(1, servicio.avisosDe(1L).size());
    }
}
