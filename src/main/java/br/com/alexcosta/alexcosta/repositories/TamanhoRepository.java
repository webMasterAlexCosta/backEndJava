package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TamanhoRepository extends JpaRepository<Tamanho, Integer> {
}
