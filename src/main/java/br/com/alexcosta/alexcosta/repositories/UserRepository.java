package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String username);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByTelefone(String telefone);
    List<User> findBySituacaoFalse();
    User findByEmailAndCpf(String email, String cpf);

}
