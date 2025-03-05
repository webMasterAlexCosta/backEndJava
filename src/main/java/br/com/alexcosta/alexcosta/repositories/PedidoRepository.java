package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.Pedido;
import br.com.alexcosta.alexcosta.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByCliente(User user);

}
