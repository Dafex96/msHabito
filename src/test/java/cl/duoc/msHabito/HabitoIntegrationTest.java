package cl.duoc.msHabito;

import cl.duoc.msHabito.client.CobranzaClient;
import cl.duoc.msHabito.dto.*;
import cl.duoc.msHabito.exception.ReglaNegocioException;
import cl.duoc.msHabito.model.TipoAviso;
import cl.duoc.msHabito.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HabitoIntegrationTest {

    @Autowired
    PagoHabitoService pagos;
    @Autowired
    EstadisticaService estadisticas;
    @Autowired
    ReconocimientoService reconocimientos;
    @Autowired
    AvisoService avisos;
    @Autowired
    RecomendacionService recomendaciones;
    @MockitoBean
    CobranzaClient cobranza;

    @Test
    void elMicroservicioCargaLosDatosDePrueba() {
        assertEquals(6, pagos.listar(1L).size());

        EstadisticaResponse e = estadisticas.deUsuario(1L);
        assertEquals(5, e.pagosPuntuales());
        assertEquals(83.3, e.porcentajePuntualidad());
        assertEquals(4, e.rachaActual());

        List<String> codigos = reconocimientos.listar(1L).stream().map(ReconocimientoResponse::codigo).toList();
        assertEquals(3, codigos.size());
        assertTrue(codigos.containsAll(List.of("PRIMER_PAGO_PUNTUAL", "RACHA_3", "PAGADOR_ANTICIPADO")));
    }

    @Test
    void registrarUnPagoPuntualOtorgaElPrimerReconocimiento() {
        LocalDate hoy = LocalDate.now();
        PagoHabitoRequest req = new PagoHabitoRequest(50L, "PF-NUEVO-1", "Enel", "Luz", 28000, hoy.plusDays(2), hoy);

        RegistroPagoResponse r = pagos.registrar(req);

        assertTrue(r.pago().puntual());
        assertEquals(1, r.reconocimientosNuevos().size());
        assertEquals("PRIMER_PAGO_PUNTUAL", r.reconocimientosNuevos().get(0).codigo());
        assertThrows(ReglaNegocioException.class, () -> pagos.registrar(req));
    }

    @Test
    void avisosYRecomendacionesParaUnUsuario() {
        LocalDate hoy = LocalDate.now(java.time.ZoneId.of("America/Santiago"));
        when(cobranza.compromisosDeUsuario(99L)).thenReturn(List.of(
                new CompromisoCobranza("PF-1", "Enel", "Luz", 28000, hoy.plusDays(2), "Pendiente", 28000)));

        List<AvisoResponse> lista = avisos.avisosDe(99L);

        assertEquals(1, lista.size());
        assertEquals(TipoAviso.PROXIMO_VENCIMIENTO, lista.get(0).tipo());
        assertEquals("INICIO", recomendaciones.recomendacionesDe(99L).get(0).tipo());
    }
}
