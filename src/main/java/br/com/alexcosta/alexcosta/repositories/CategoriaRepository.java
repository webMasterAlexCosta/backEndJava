package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
}
