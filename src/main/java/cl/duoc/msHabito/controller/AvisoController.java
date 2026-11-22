package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.AvisoResponse;
import cl.duoc.msHabito.dto.RecomendacionResponse;
import cl.duoc.msHabito.service.AvisoService;
import cl.duoc.msHabito.service.RecomendacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habito")
@RequiredArgsConstructor
@Tag(name = "Avisos y recomendaciones", description = "Avisos de vencimiento y consejos personalizados")
public class AvisoController {

    private final AvisoService avisos;
    private final RecomendacionService recomendaciones;

    @GetMapping("/avisos")
    @Operation(summary = "Avisos de vencimiento de un usuario",
            description = "Compromisos vencidos, que vencen hoy o que vencen dentro de los días de anticipación "
                    + "configurados. Consulta los compromisos al microservicio de Cobranza.")
    @ApiResponse(responseCode = "503", description = "El servicio de cobranza no está disponible")
    public List<AvisoResponse> avisos(@RequestParam Long usuarioId) {
        return avisos.avisosDe(usuarioId);
    }

    @GetMapping("/recomendaciones")
    @Operation(summary = "Recomendaciones personalizadas",
            description = "Consejos según la puntualidad, la racha y las preferencias de aviso del usuario.")
    public List<RecomendacionResponse> recomendaciones(@RequestParam Long usuarioId) {
        return recomendaciones.recomendacionesDe(usuarioId);
    }
}
