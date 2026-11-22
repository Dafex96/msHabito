package cl.duoc.msHabito.controller;

import cl.duoc.msHabito.dto.CatalogoReconocimientoResponse;
import cl.duoc.msHabito.dto.ReconocimientoResponse;
import cl.duoc.msHabito.service.ReconocimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habito/reconocimientos")
@RequiredArgsConstructor
@Tag(name = "Reconocimientos", description = "Insignias por pagar a tiempo")
public class ReconocimientoController {

    private final ReconocimientoService reconocimientos;

    @GetMapping
    @Operation(summary = "Reconocimientos obtenidos por un usuario", description = "Del más reciente al más antiguo.")
    public List<ReconocimientoResponse> listar(@RequestParam Long usuarioId) {
        return reconocimientos.listar(usuarioId);
    }

    @GetMapping("/catalogo")
    @Operation(summary = "Catálogo de reconocimientos",
            description = "Todos los reconocimientos posibles, indicando cuáles ya obtuvo el usuario.")
    public List<CatalogoReconocimientoResponse> catalogo(@RequestParam Long usuarioId) {
        return reconocimientos.catalogo(usuarioId);
    }
}
