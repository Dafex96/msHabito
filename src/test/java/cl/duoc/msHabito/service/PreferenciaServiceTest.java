package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.PreferenciaRequest;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.model.PreferenciaAviso;
import cl.duoc.msHabito.repository.PreferenciaAvisoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenciaServiceTest {

    @Mock
    PreferenciaAvisoRepository preferencias;
    @InjectMocks
    PreferenciaService servicio;

    @Test
    void sinConfiguracionEntregaLosValoresPorDefecto() {
        when(preferencias.findByUsuarioId(7L)).thenReturn(Optional.empty());

        PreferenciaResponse r = servicio.obtener(7L);

        assertEquals(7L, r.usuarioId());
        assertTrue(r.avisosActivos());
        assertEquals(3, r.diasAnticipacion());
    }

    @Test
    void entregaLaConfiguracionGuardada() {
        when(preferencias.findByUsuarioId(7L)).thenReturn(Optional.of(
                PreferenciaAviso.builder().usuarioId(7L).avisosActivos(false).diasAnticipacion(10).build()));

        PreferenciaResponse r = servicio.obtener(7L);

        assertFalse(r.avisosActivos());
        assertEquals(10, r.diasAnticipacion());
    }

    @Test
    void guardaUnaConfiguracionNueva() {
        when(preferencias.findByUsuarioId(7L)).thenReturn(Optional.empty());
        when(preferencias.save(any(PreferenciaAviso.class))).thenAnswer(inv -> inv.getArgument(0));

        PreferenciaResponse r = servicio.guardar(7L, new PreferenciaRequest(false, 5));

        assertEquals(7L, r.usuarioId());
        assertFalse(r.avisosActivos());
        assertEquals(5, r.diasAnticipacion());
    }

    @Test
    void actualizaLaConfiguracionExistente() {
        PreferenciaAviso existente = PreferenciaAviso.builder().id(1L).usuarioId(7L).build();
        when(preferencias.findByUsuarioId(7L)).thenReturn(Optional.of(existente));
        when(preferencias.save(any(PreferenciaAviso.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.guardar(7L, new PreferenciaRequest(true, 8));

        assertEquals(8, existente.getDiasAnticipacion());
        assertEquals(1L, existente.getId());
    }
}
