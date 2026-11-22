package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.*;
import cl.duoc.msHabito.service.EstadisticaService;
import cl.duoc.msHabito.service.PagoHabitoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habito")
@RequiredArgsConstructor
@Tag(name = "Hábito de pago", description = "Historial de pagos y estadísticas de puntualidad")
public class PagoHabitoController {

    private final PagoHabitoService pagos;
    private final EstadisticaService estadisticas;

    @PostMapping("/pagos")
    @Operation(summary = "Registrar un pago realizado",
            description = "Guarda el pago y entrega los reconocimientos nuevos que el usuario gana con él.")
    @ApiResponse(responseCode = "409", description = "El pago de ese folio ya fue registrado")
    public ResponseEntity<RegistroPagoResponse> registrar(@Valid @RequestBody PagoHabitoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagos.registrar(req));
    }

    @GetMapping("/pagos")
    @Operation(summary = "Historial de pagos de un usuario", description = "Del más reciente al más antiguo.")
    public List<PagoHabitoResponse> listar(@RequestParam Long usuarioId) {
        return pagos.listar(usuarioId);
    }

    @GetMapping("/estadisticas/{usuarioId}")
    @Operation(summary = "Estadísticas de puntualidad",
            description = "Porcentaje de pagos puntuales, racha actual y mejor racha, y promedio de días de anticipación.")
    public EstadisticaResponse estadisticas(@PathVariable Long usuarioId) {
        return estadisticas.deUsuario(usuarioId);
    }
}
