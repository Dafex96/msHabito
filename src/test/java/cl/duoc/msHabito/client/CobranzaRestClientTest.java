package cl.duoc.msHabito.client;

import cl.duoc.msHabito.dto.CompromisoCobranza;
import cl.duoc.msHabito.exception.ServicioNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;

class CobranzaRestClientTest {

    private static final String URL = "http://msCobranza/api/cobranza/compromisos?usuarioId=1";

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
    private final CobranzaRestClient cliente = new CobranzaRestClient(builder, "http://msCobranza");

    @Test
    void leeLosCompromisosIgnorandoLosCamposQueNoUsa() {
        String json = "[{\"folio\":\"PF-1\",\"usuarioId\":1,\"rutUsuario\":\"1-9\",\"empresa\":\"Enel\","
                + "\"concepto\":\"Luz\",\"monto\":28000,\"vencimiento\":\"2026-12-01\",\"estado\":\"Pendiente\","
                + "\"recargo\":1100,\"numeroCuota\":\"\",\"montoAPagar\":28000}]";
        servidor.expect(requestTo(URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<CompromisoCobranza> lista = cliente.compromisosDeUsuario(1L);

        assertEquals(1, lista.size());
        assertEquals("PF-1", lista.get(0).folio());
        assertEquals(LocalDate.of(2026, 12, 1), lista.get(0).vencimiento());
        assertEquals("Pendiente", lista.get(0).estado());
        assertEquals(28000, lista.get(0).montoAPagar());
        servidor.verify();
    }

    @Test
    void sinContenidoDevuelveListaVacia() {
        servidor.expect(requestTo(URL)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        assertTrue(cliente.compromisosDeUsuario(1L).isEmpty());
    }

    @Test
    void siCobranzaFallaLanzaServicioNoDisponible() {
        servidor.expect(requestTo(URL)).andRespond(withServerError());
        assertThrows(ServicioNoDisponibleException.class, () -> cliente.compromisosDeUsuario(1L));
    }
}
