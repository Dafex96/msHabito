package cl.duoc.msHabito.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferencias_aviso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaAviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencia")
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "avisos_activos", nullable = false)
    @Builder.Default
    private Boolean avisosActivos = true;

    @Column(name = "dias_anticipacion", nullable = false)
    @Builder.Default
    private Integer diasAnticipacion = 3;
}
