package cl.duoc.msHabito.client;

import cl.duoc.msHabito.dto.CompromisoCobranza;
import cl.duoc.msHabito.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class CobranzaRestClient implements CobranzaClient {

    private final RestClient restClient;

    public CobranzaRestClient(RestClient.Builder builder,
                              @Value("${habito.cobranza.url:http://msCobranza}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public List<CompromisoCobranza> compromisosDeUsuario(Long usuarioId) {
        try {
            List<CompromisoCobranza> respuesta = restClient.get()
                    .uri(uri -> uri.path("/api/cobranza/compromisos").queryParam("usuarioId", usuarioId).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CompromisoCobranza>>() {
                    });
            return respuesta == null ? List.of() : respuesta;
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No fue posible consultar el servicio de cobranza", e);
        }
    }
}
