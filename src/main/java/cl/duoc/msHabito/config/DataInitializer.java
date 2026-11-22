package cl.duoc.msHabito.config;

import cl.duoc.msHabito.model.PagoHabito;
import cl.duoc.msHabito.model.PreferenciaAviso;
import cl.duoc.msHabito.repository.PagoHabitoRepository;
import cl.duoc.msHabito.repository.PreferenciaAvisoRepository;
import cl.duoc.msHabito.service.ReconocimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "habito.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private static final long USUARIO_ID = 1L;

    private final PagoHabitoRepository pagos;
    private final PreferenciaAvisoRepository preferencias;
    private final ReconocimientoService reconocimientos;
    private final Clock clock;

    @Override
    public void run(String... args) {
        if (pagos.count() > 0) {
            return;
        }
        LocalDate hoy = LocalDate.now(clock);
        pagos.saveAll(List.of(
                pago("PF-HIST-001", "Enel", "Luz", 27500, hoy.minusDays(150), 3),
                pago("PF-HIST-002", "Aguas Andinas", "Agua", 33000, hoy.minusDays(120), -2),
                pago("PF-HIST-003", "Condominio", "Gasto común", 45000, hoy.minusDays(90), 5),
                pago("PF-HIST-004", "Duoc Uc", "Mensualidad", 356000, hoy.minusDays(60), 6),
                pago("PF-HIST-005", "Enel", "Luz", 28000, hoy.minusDays(30), 5),
                pago("PF-HIST-006", "Aguas Andinas", "Agua", 34500, hoy.minusDays(10), 1)
        ));
        preferencias.save(PreferenciaAviso.builder().usuarioId(USUARIO_ID).avisosActivos(true).diasAnticipacion(3).build());
        reconocimientos.evaluar(USUARIO_ID);
        log.info("Datos de prueba de hábitos cargados");
    }

    private PagoHabito pago(String folio, String empresa, String concepto, int monto, LocalDate vencimiento, int diasAnticipacion) {
        return PagoHabito.builder()
                .usuarioId(USUARIO_ID).folio(folio).empresa(empresa).concepto(concepto).monto(monto)
                .vencimiento(vencimiento).fechaPago(vencimiento.minusDays(diasAnticipacion))
                .build();
    }
}
