package cl.duoc.msHabito.exception;

/** Otro microservicio (Cobranza) no respondió. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
