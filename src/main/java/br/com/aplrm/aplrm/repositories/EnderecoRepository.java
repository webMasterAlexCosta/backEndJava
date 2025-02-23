package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco,Integer> {
}
