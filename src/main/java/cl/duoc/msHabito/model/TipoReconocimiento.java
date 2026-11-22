package cl.duoc.msHabito.model;

import cl.duoc.msHabito.dto.EstadisticaResponse;

import java.util.function.Predicate;

public enum TipoReconocimiento {
    PRIMER_PAGO_PUNTUAL("Primer pago puntual", "Pagaste un compromiso a tiempo por primera vez.",
            e -> e.pagosPuntuales() >= 1),
    RACHA_3("Racha de 3", "Pagaste 3 compromisos seguidos a tiempo.",
            e -> e.mejorRacha() >= 3),
    RACHA_6("Racha de 6", "Pagaste 6 compromisos seguidos a tiempo.",
            e -> e.mejorRacha() >= 6),
    PAGADOR_ANTICIPADO("Pagador anticipado", "Pagaste 3 compromisos con 5 o más días de anticipación.",
            e -> e.pagosAnticipados() >= 3),
    PUNTUALIDAD_IMPECABLE("Puntualidad impecable", "Pagaste a tiempo todos tus compromisos (mínimo 5 pagos).",
            e -> e.totalPagos() >= 5 && e.pagosPuntuales() == e.totalPagos());

    private final String titulo;
    private final String descripcion;
    private final Predicate<EstadisticaResponse> condicion;

    TipoReconocimiento(String titulo, String descripcion, Predicate<EstadisticaResponse> condicion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.condicion = condicion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean cumple(EstadisticaResponse estadistica) {
        return condicion.test(estadistica);
    }
}
