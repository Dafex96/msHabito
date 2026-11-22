package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.AvisoResponse;
import cl.duoc.msHabito.dto.RecomendacionResponse;
import cl.duoc.msHabito.model.TipoAviso;
import cl.duoc.msHabito.service.AvisoService;
import cl.duoc.msHabito.service.RecomendacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvisoControllerTest {

    @Mock
    AvisoService avisos;
    @Mock
    RecomendacionService recomendaciones;
    @InjectMocks
    AvisoController controller;

    @Test
    void avisos() {
        when(avisos.avisosDe(1L)).thenReturn(List.of(new AvisoResponse(TipoAviso.VENCE_HOY, "PF-1", "Enel", "Luz",
                LocalDate.of(2026, 11, 22), 0, 1000, "Hoy vence")));
        assertEquals(TipoAviso.VENCE_HOY, controller.avisos(1L).get(0).tipo());
    }

    @Test
    void recomendaciones() {
        when(recomendaciones.recomendacionesDe(1L)).thenReturn(List.of(new RecomendacionResponse("INICIO", "t", "m")));
        assertEquals("INICIO", controller.recomendaciones(1L).get(0).tipo());
    }
}
