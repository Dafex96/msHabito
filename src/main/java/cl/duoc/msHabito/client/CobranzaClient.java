package cl.duoc.msHabito.client;

import cl.duoc.msHabito.dto.CompromisoCobranza;

import java.util.List;

public interface CobranzaClient {

    List<CompromisoCobranza> compromisosDeUsuario(Long usuarioId);
}
