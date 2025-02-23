package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    Optional<Perfil> findByAuthority(String authority);
}
