package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco,Integer> {
}
