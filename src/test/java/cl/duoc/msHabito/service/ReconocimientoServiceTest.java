package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.CatalogoReconocimientoResponse;
import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.model.Reconocimiento;
import cl.duoc.msHabito.model.TipoReconocimiento;
import cl.duoc.msHabito.repository.ReconocimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReconocimientoServiceTest {

    @Mock
    ReconocimientoRepository reconocimientos;
    @Mock
    EstadisticaService estadisticas;

    ReconocimientoService servicio;

    @BeforeEach
    void setUp() {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-22T15:00:00Z"), ZoneId.of("America/Santiago"));
        servicio = new ReconocimientoService(reconocimientos, estadisticas, reloj);
        when(reconocimientos.save(any(Reconocimiento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(1L)).thenReturn(List.of());
    }

    private EstadisticaResponse stats(int total, int puntuales, int anticipados, int actual, int mejor) {
        double pct = total == 0 ? 0 : puntuales * 100.0 / total;
        return new EstadisticaResponse(total, puntuales, anticipados, pct, actual, mejor, 2.0);
    }

    private List<TipoReconocimiento> tipos(List<Reconocimiento> lista) {
        return lista.stream().map(Reconocimiento::getCodigo).toList();
    }

    @Test
    void otorgaLosReconocimientosQueCumple() {
        when(estadisticas.deUsuario(1L)).thenReturn(stats(6, 5, 3, 4, 4));

        List<Reconocimiento> nuevos = servicio.evaluar(1L);

        assertEquals(List.of(TipoReconocimiento.PRIMER_PAGO_PUNTUAL, TipoReconocimiento.RACHA_3,
                TipoReconocimiento.PAGADOR_ANTICIPADO), tipos(nuevos));
        assertEquals(LocalDateTime.of(2026, 11, 22, 12, 0), nuevos.get(0).getFechaOtorgado());
    }

    @Test
    void noRepiteLosQueYaTenia() {
        when(estadisticas.deUsuario(1L)).thenReturn(stats(6, 5, 3, 4, 4));
        when(reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(1L)).thenReturn(List.of(
                Reconocimiento.builder().usuarioId(1L).codigo(TipoReconocimiento.RACHA_3).build()));

        List<Reconocimiento> nuevos = servicio.evaluar(1L);

        assertEquals(List.of(TipoReconocimiento.PRIMER_PAGO_PUNTUAL, TipoReconocimiento.PAGADOR_ANTICIPADO), tipos(nuevos));
    }

    @Test
    void sinPagosNoOtorgaNada() {
        when(estadisticas.deUsuario(1L)).thenReturn(stats(0, 0, 0, 0, 0));
        assertTrue(servicio.evaluar(1L).isEmpty());
        verify(reconocimientos, never()).save(any());
    }

    @Test
    void puntualidadImpecableYRachaDeSeis() {
        when(estadisticas.deUsuario(1L)).thenReturn(stats(6, 6, 0, 6, 6));

        List<TipoReconocimiento> tipos = tipos(servicio.evaluar(1L));

        assertTrue(tipos.contains(TipoReconocimiento.RACHA_6));
        assertTrue(tipos.contains(TipoReconocimiento.PUNTUALIDAD_IMPECABLE));
        assertFalse(tipos.contains(TipoReconocimiento.PAGADOR_ANTICIPADO));
    }

    @Test
    void listarConvierteALaRespuesta() {
        when(reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(1L)).thenReturn(List.of(
                Reconocimiento.builder().usuarioId(1L).codigo(TipoReconocimiento.RACHA_3)
                        .fechaOtorgado(LocalDateTime.of(2026, 11, 1, 10, 0)).build()));

        var lista = servicio.listar(1L);

        assertEquals(1, lista.size());
        assertEquals("RACHA_3", lista.get(0).codigo());
        assertEquals("Racha de 3", lista.get(0).titulo());
    }

    @Test
    void catalogoMarcaLosObtenidos() {
        when(reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(1L)).thenReturn(List.of(
                Reconocimiento.builder().usuarioId(1L).codigo(TipoReconocimiento.PRIMER_PAGO_PUNTUAL).build()));

        List<CatalogoReconocimientoResponse> catalogo = servicio.catalogo(1L);

        assertEquals(TipoReconocimiento.values().length, catalogo.size());
        assertTrue(catalogo.stream().filter(c -> c.codigo().equals("PRIMER_PAGO_PUNTUAL")).findFirst().orElseThrow().obtenido());
        assertEquals(1, catalogo.stream().filter(CatalogoReconocimientoResponse::obtenido).count());
    }
}
