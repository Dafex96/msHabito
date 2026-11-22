package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.dto.RecomendacionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceTest {

    @Mock
    EstadisticaService estadisticas;
    @Mock
    PreferenciaService preferencias;
    @InjectMocks
    RecomendacionService servicio;

    private void datos(EstadisticaResponse e, boolean avisos, int dias) {
        when(estadisticas.deUsuario(1L)).thenReturn(e);
        when(preferencias.obtener(1L)).thenReturn(new PreferenciaResponse(1L, avisos, dias));
    }

    private List<String> tipos() {
        return servicio.recomendacionesDe(1L).stream().map(RecomendacionResponse::tipo).toList();
    }

    @Test
    void sinHistorialInvitaAPagarATiempo() {
        datos(new EstadisticaResponse(0, 0, 0, 0, 0, 0, 0), true, 3);
        assertEquals(List.of("INICIO"), tipos());
    }

    @Test
    void sinHistorialYSinAvisosSugiereActivarlos() {
        datos(new EstadisticaResponse(0, 0, 0, 0, 0, 0, 0), false, 3);
        assertEquals(List.of("AVISOS", "INICIO"), tipos());
    }

    @Test
    void conBajaPuntualidadSugiereMasAnticipacion() {
        datos(new EstadisticaResponse(4, 2, 0, 50, 1, 1, 2.0), true, 3);

        List<RecomendacionResponse> lista = servicio.recomendacionesDe(1L);

        assertEquals("ANTICIPACION", lista.get(0).tipo());
        assertTrue(lista.get(0).mensaje().contains("5 días de anticipación"));
        assertTrue(lista.get(0).mensaje().contains("50%"));
    }

    @Test
    void conBajaPuntualidadYMuchaAnticipacionSugierePagarAlRecibirElAviso() {
        datos(new EstadisticaResponse(4, 2, 0, 50, 1, 1, 2.0), true, 7);
        assertTrue(servicio.recomendacionesDe(1L).get(0).mensaje().contains("apenas recibas el aviso"));
    }

    @Test
    void conExcelentePuntualidadFelicitaYMantieneLaRacha() {
        datos(new EstadisticaResponse(5, 5, 3, 100, 5, 5, 4.0), true, 3);

        List<RecomendacionResponse> lista = servicio.recomendacionesDe(1L);

        assertEquals(List.of("FELICITACION", "RACHA"), lista.stream().map(RecomendacionResponse::tipo).toList());
        assertEquals("Mantén tu racha", lista.get(1).titulo());
    }

    @Test
    void siElUltimoPagoFueTardeSugiereRetomarLaRacha() {
        datos(new EstadisticaResponse(4, 3, 1, 75, 0, 3, 2.0), true, 3);

        List<RecomendacionResponse> lista = servicio.recomendacionesDe(1L);

        assertEquals(List.of("RACHA"), lista.stream().map(RecomendacionResponse::tipo).toList());
        assertEquals("Retoma tu racha", lista.get(0).titulo());
    }

    @Test
    void siPagaElMismoDiaSugierePagarConMasMargen() {
        datos(new EstadisticaResponse(4, 3, 0, 75, 1, 3, 0.5), true, 3);
        assertEquals(List.of("MARGEN"), tipos());
    }
}
