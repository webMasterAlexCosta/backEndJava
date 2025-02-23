package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TamanhoRepository extends JpaRepository<Tamanho, Integer> {
}
