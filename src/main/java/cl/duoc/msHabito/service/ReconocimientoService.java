package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.CatalogoReconocimientoResponse;
import cl.duoc.msHabito.dto.EstadisticaResponse;
import cl.duoc.msHabito.dto.ReconocimientoResponse;
import cl.duoc.msHabito.model.Reconocimiento;
import cl.duoc.msHabito.model.TipoReconocimiento;
import cl.duoc.msHabito.repository.ReconocimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReconocimientoService {

    private final ReconocimientoRepository reconocimientos;
    private final EstadisticaService estadisticas;
    private final Clock clock;

    @Transactional
    public List<Reconocimiento> evaluar(Long usuarioId) {
        EstadisticaResponse estadistica = estadisticas.deUsuario(usuarioId);
        Set<TipoReconocimiento> obtenidos = codigosObtenidos(usuarioId);

        List<Reconocimiento> nuevos = new ArrayList<>();
        for (TipoReconocimiento tipo : TipoReconocimiento.values()) {
            if (!obtenidos.contains(tipo) && tipo.cumple(estadistica)) {
                nuevos.add(reconocimientos.save(Reconocimiento.builder()
                        .usuarioId(usuarioId)
                        .codigo(tipo)
                        .fechaOtorgado(LocalDateTime.now(clock))
                        .build()));
            }
        }
        return nuevos;
    }

    @Transactional(readOnly = true)
    public List<ReconocimientoResponse> listar(Long usuarioId) {
        return reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(usuarioId).stream()
                .map(ReconocimientoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogoReconocimientoResponse> catalogo(Long usuarioId) {
        Set<TipoReconocimiento> obtenidos = codigosObtenidos(usuarioId);
        List<CatalogoReconocimientoResponse> catalogo = new ArrayList<>();
        for (TipoReconocimiento tipo : TipoReconocimiento.values()) {
            catalogo.add(new CatalogoReconocimientoResponse(tipo.name(), tipo.getTitulo(),
                    tipo.getDescripcion(), obtenidos.contains(tipo)));
        }
        return catalogo;
    }

    private Set<TipoReconocimiento> codigosObtenidos(Long usuarioId) {
        return reconocimientos.findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(usuarioId).stream()
                .map(Reconocimiento::getCodigo)
                .collect(Collectors.toSet());
    }
}
