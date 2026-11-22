package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.PreferenciaRequest;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.service.PreferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/habito/preferencias")
@RequiredArgsConstructor
@Tag(name = "Preferencias de aviso", description = "Configuración de los avisos de vencimiento")
public class PreferenciaController {

    private final PreferenciaService preferencias;

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Obtener las preferencias de aviso",
            description = "Si el usuario no ha configurado nada, devuelve los valores por defecto (avisos activos, 3 días).")
    public PreferenciaResponse obtener(@PathVariable Long usuarioId) {
        return preferencias.obtener(usuarioId);
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Guardar las preferencias de aviso")
    public PreferenciaResponse guardar(@PathVariable Long usuarioId, @Valid @RequestBody PreferenciaRequest req) {
        return preferencias.guardar(usuarioId, req);
    }
}
