package br.com.alexcosta.alexcosta.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_pedido_item")
public class PedidoItem {

	@EmbeddedId
	private PedidoItemPK id = new PedidoItemPK();

	private Integer quantidade;
	private Double preco;

	public PedidoItem(Pedido pedido, Produto produto, Integer quantidade, Double preco, Integer tamanho) {
		this.preco = preco;
		this.quantidade = quantidade;
		id.setPedido(pedido);
		id.setProduto(produto);
		id.setTamanho(tamanho);
	}
}
