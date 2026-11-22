package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.*;
import cl.duoc.msHabito.exception.ReglaNegocioException;
import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.model.Reconocimiento;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoHabitoService {

    private final PagoHabitoRepository pagos;
    private final ReconocimientoService reconocimientos;

    @Transactional
    public RegistroPagoResponse registrar(PagoHabitoRequest req) {
        String folio = req.folio().trim();
        if (pagos.existsByFolio(folio)) {
            throw new ReglaNegocioException("Ya se registró el pago del folio " + folio);
        }
        PagoHabito pago = pagos.save(PagoHabito.builder()
                .usuarioId(req.usuarioId())
                .folio(folio)
                .empresa(req.empresa().trim())
                .concepto(req.concepto().trim())
                .monto(req.monto())
                .vencimiento(req.vencimiento())
                .fechaPago(req.fechaPago())
                .build());

        List<Reconocimiento> nuevos = reconocimientos.evaluar(req.usuarioId());
        return new RegistroPagoResponse(PagoHabitoResponse.desde(pago),
                nuevos.stream().map(ReconocimientoResponse::desde).toList());
    }

    @Transactional(readOnly = true)
    public List<PagoHabitoResponse> listar(Long usuarioId) {
        return pagos.findByUsuarioIdOrderByFechaPagoDescIdDesc(usuarioId).stream()
                .map(PagoHabitoResponse::desde)
                .toList();
    }
}
