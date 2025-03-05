package br.com.alexcosta.alexcosta.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class UserVerificador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID uuid;
    private Instant dataExpiracao;

    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id",unique = true)
    private User user;


}
