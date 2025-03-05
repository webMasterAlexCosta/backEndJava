package br.com.alexcosta.alexcosta.repositories;

import br.com.alexcosta.alexcosta.entities.PedidoItem;
import br.com.alexcosta.alexcosta.entities.PedidoItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemPK> {
}
