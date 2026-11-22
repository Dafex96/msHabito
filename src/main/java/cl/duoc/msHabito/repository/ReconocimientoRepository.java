package cl.duoc.msHabito.repository;

import cl.duoc.msHabito.model.Reconocimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconocimientoRepository extends JpaRepository<Reconocimiento, Long> {

    List<Reconocimiento> findByUsuarioIdOrderByFechaOtorgadoDescIdDesc(Long usuarioId);
}
