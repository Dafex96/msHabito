package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.PreferenciaRequest;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.service.PreferenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenciaControllerTest {

    @Mock
    PreferenciaService servicio;
    @InjectMocks
    PreferenciaController controller;

    @Test
    void obtener() {
        when(servicio.obtener(1L)).thenReturn(new PreferenciaResponse(1L, true, 3));
        assertEquals(3, controller.obtener(1L).diasAnticipacion());
    }

    @Test
    void guardar() {
        PreferenciaRequest req = new PreferenciaRequest(false, 5);
        when(servicio.guardar(1L, req)).thenReturn(new PreferenciaResponse(1L, false, 5));
        assertEquals(5, controller.guardar(1L, req).diasAnticipacion());
    }
}
