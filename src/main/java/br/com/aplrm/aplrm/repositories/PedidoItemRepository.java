package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.PedidoItem;
import br.com.aplrm.aplrm.entities.PedidoItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemPK> {
}
