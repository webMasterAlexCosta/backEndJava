package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.UserVerificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVerificadorRepository extends JpaRepository<UserVerificador,Long> {
    Optional<UserVerificador> findByUuid(UUID uuid);
    List<UserVerificador> findByDataExpiracaoBefore(Instant dataExpiracao);

}
