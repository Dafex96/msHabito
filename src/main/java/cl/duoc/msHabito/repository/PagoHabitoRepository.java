package cl.duoc.msHabito.repository;

import cl.duoc.msHabito.model.PagoHabito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoHabitoRepository extends JpaRepository<PagoHabito, Long> {

    boolean existsByFolio(String folio);

    List<PagoHabito> findByUsuarioIdOrderByVencimientoAscFechaPagoAscIdAsc(Long usuarioId);

    List<PagoHabito> findByUsuarioIdOrderByFechaPagoDescIdDesc(Long usuarioId);
}
