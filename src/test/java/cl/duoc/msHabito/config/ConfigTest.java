package cl.duoc.msHabito.config;

import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.model.PreferenciaAviso;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import cl.duoc.msHabito.repository.PreferenciaAvisoRepository;
import cl.duoc.msHabito.service.ReconocimientoService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-22T15:00:00Z"), ZoneId.of("America/Santiago"));
    private static final LocalDate HOY = LocalDate.of(2026, 11, 22);

    @Mock
    PagoHabitoRepository pagos;
    @Mock
    PreferenciaAvisoRepository preferencias;
    @Mock
    ReconocimientoService reconocimientos;

    @Test
    void relojUsaLaZonaDeChile() {
        assertEquals(ZoneId.of("America/Santiago"), new AppConfig().clock().getZone());
    }

    @Test
    void creaElBuilderDeRestClient() {
        assertNotNull(new AppConfig().restClientBuilder());
    }

    @Test
    void swaggerTieneTituloYVersion() {
        OpenAPI api = new SwaggerConfig().swaggerInfo();
        assertEquals("PagoFlex - Microservicio de Hábitos", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
    }

    @Test
    void cargaLosDatosDePruebaSiNoHayPagos() {
        when(pagos.count()).thenReturn(0L);

        new DataInitializer(pagos, preferencias, reconocimientos, RELOJ).run();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<PagoHabito>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(pagos).saveAll(captor.capture());
        List<PagoHabito> cargados = new ArrayList<>();
        captor.getValue().forEach(cargados::add);

        assertEquals(6, cargados.size());
        assertEquals(5, cargados.stream().filter(PagoHabito::puntual).count());
        assertTrue(cargados.stream().allMatch(p -> p.getUsuarioId() == 1L && p.getVencimiento().isBefore(HOY)));
        verify(preferencias).save(any(PreferenciaAviso.class));
        verify(reconocimientos).evaluar(1L);
    }

    @Test
    void noVuelveACargarSiYaHayPagos() {
        when(pagos.count()).thenReturn(6L);

        new DataInitializer(pagos, preferencias, reconocimientos, RELOJ).run();

        verify(pagos, never()).saveAll(any());
        verifyNoInteractions(preferencias, reconocimientos);
    }
}
