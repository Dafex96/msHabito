package cl.duoc.msHabito.repository;

import cl.duoc.msHabito.model.PreferenciaAviso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaAvisoRepository extends JpaRepository<PreferenciaAviso, Long> {

    Optional<PreferenciaAviso> findByUsuarioId(Long usuarioId);
}
