package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.CatalogoReconocimientoResponse;
import cl.duoc.msHabito.dto.ReconocimientoResponse;
import cl.duoc.msHabito.service.ReconocimientoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconocimientoControllerTest {

    @Mock
    ReconocimientoService servicio;
    @InjectMocks
    ReconocimientoController controller;

    @Test
    void listar() {
        when(servicio.listar(1L)).thenReturn(List.of(
                new ReconocimientoResponse("RACHA_3", "Racha de 3", "desc", LocalDateTime.of(2026, 11, 1, 10, 0))));
        assertEquals("RACHA_3", controller.listar(1L).get(0).codigo());
    }

    @Test
    void catalogo() {
        when(servicio.catalogo(1L)).thenReturn(List.of(new CatalogoReconocimientoResponse("RACHA_3", "Racha de 3", "desc", true)));
        assertEquals(1, controller.catalogo(1L).size());
    }
}
