package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
}
