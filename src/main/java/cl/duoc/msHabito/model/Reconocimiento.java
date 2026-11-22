package cl.duoc.msHabito.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Reconocimiento (insignia) ya otorgado a un usuario. Un mismo reconocimiento se otorga una sola vez. */
@Entity
@Table(name = "reconocimientos", uniqueConstraints =
        @UniqueConstraint(name = "uk_reconocimiento_usuario_codigo", columnNames = {"usuario_id", "codigo"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reconocimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reconocimiento")
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoReconocimiento codigo;

    @Column(name = "fecha_otorgado", nullable = false)
    private LocalDateTime fechaOtorgado;
}
