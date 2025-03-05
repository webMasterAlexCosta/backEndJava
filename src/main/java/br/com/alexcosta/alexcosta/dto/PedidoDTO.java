package br.com.alexcosta.alexcosta.dto;

import br.com.alexcosta.alexcosta.entities.Pedido;
import br.com.alexcosta.alexcosta.entities.PedidoItem;
import br.com.alexcosta.alexcosta.entities.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Integer id;

    private Instant momento;
    private StatusPedido statusPedido;

    private String numeroPedido;
    private UserDTO client;
    private List<PedidoItemDTO> items = new ArrayList<>();

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.momento = pedido.getMomento();
        this.statusPedido = pedido.getStatus();
        this.client = new UserDTO(pedido.getCliente());
        for (PedidoItem item : pedido.getItems()) {
            PedidoItemDTO itemDTO = new PedidoItemDTO(item.getId().getProduto().getId(), item.getPreco(), item.getQuantidade(), item.getId().getTamanho(), item.getId().getProduto().getImgUrl());
            items.add(itemDTO);
        }
        this.numeroPedido=pedido.getNumeroPedido();
    }

      public Double getTotal() {
        double soma = 0;
        for (PedidoItemDTO item : items) {
            soma += item.getSubTotal();
        }
        return soma;
    }
}
