package cl.duoc.msHabito.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void reglaDeNegocioEs409() {
        ProblemDetail p = handler.reglaNegocio(new ReglaNegocioException("repetido"));
        assertEquals(409, p.getStatus());
        assertEquals("repetido", p.getDetail());
    }

    @Test
    void servicioNoDisponibleEs503() {
        ProblemDetail p = handler.servicioNoDisponible(new ServicioNoDisponibleException("caído", new RuntimeException()));
        assertEquals(503, p.getStatus());
        assertEquals("caído", p.getDetail());
    }

    @Test
    void argumentoInvalidoEs400() {
        ProblemDetail p = handler.argumentoInvalido(new IllegalArgumentException("malo"));
        assertEquals(400, p.getStatus());
        assertEquals("malo", p.getDetail());
    }
}
