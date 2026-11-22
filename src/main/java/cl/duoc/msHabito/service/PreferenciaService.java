package cl.duoc.msHabito.service;

import cl.duoc.msHabito.dto.PreferenciaRequest;
import cl.duoc.msHabito.dto.PreferenciaResponse;
import cl.duoc.msHabito.model.PreferenciaAviso;
import cl.duoc.msHabito.repository.PreferenciaAvisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenciaService {

    static final int DIAS_POR_DEFECTO = 3;

    private final PreferenciaAvisoRepository preferencias;

    @Transactional(readOnly = true)
    public PreferenciaResponse obtener(Long usuarioId) {
        return preferencias.findByUsuarioId(usuarioId)
                .map(PreferenciaResponse::desde)
                .orElseGet(() -> new PreferenciaResponse(usuarioId, true, DIAS_POR_DEFECTO));
    }

    @Transactional
    public PreferenciaResponse guardar(Long usuarioId, PreferenciaRequest req) {
        PreferenciaAviso preferencia = preferencias.findByUsuarioId(usuarioId)
                .orElseGet(() -> PreferenciaAviso.builder().usuarioId(usuarioId).build());
        preferencia.setAvisosActivos(req.avisosActivos());
        preferencia.setDiasAnticipacion(req.diasAnticipacion());
        return PreferenciaResponse.desde(preferencias.save(preferencia));
    }
}
