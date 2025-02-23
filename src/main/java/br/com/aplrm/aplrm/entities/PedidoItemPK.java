package br.com.aplrm.aplrm.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class PedidoItemPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "tamanho_id")
    private Integer tamanho;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoItemPK that = (PedidoItemPK) o;
        return Objects.equals(pedido, that.pedido) &&
                Objects.equals(produto, that.produto) &&
                Objects.equals(tamanho, that.tamanho);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, produto, tamanho);
    }
}
