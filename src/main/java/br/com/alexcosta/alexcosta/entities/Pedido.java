//package br.com.aplrm.aplrm.entities;
//
//import jakarta.persistence.*;
//
//import java.io.Serializable;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//
//@Entity
//@Table(name="tb_pedido")
//public class Pedido implements Serializable {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Integer id;
//
//	//@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
//	private LocalDateTime momento;
//	private StatusPedido status;
//
//	@ManyToOne
//	@JoinColumn(name="cliente_id")
//	private User cliente;
//
//	@OneToOne(mappedBy="pedido",cascade=CascadeType.ALL)
//	private Pagamento pagamento;
//
//	@OneToMany(mappedBy = "id.pedido")
//	private Set<PedidoItem> items=new HashSet<>();
//
//	public Pedido(){}
//	public Pedido(Integer id, LocalDateTime momento, StatusPedido status, User cliente, Pagamento pagamento) {
//		this.id = id;
//		this.momento = momento;
//		this.status = status;
//		this.cliente = cliente;
//		this.pagamento = pagamento;
//	}
//
//	public Integer getId() {
//		return id;
//	}
//	public void setId(Integer id) {
//		this.id = id;
//	}
//	public LocalDateTime getMomento() {
//		return momento;
//	}
//	public void setMomento(LocalDateTime momento) {
//		this.momento = momento;
//	}
//	public List<Produto> getProdutos(){
//		return items.stream().map(x-> x.getProduto()).toList();
//	}
//
//	public StatusPedido getStatus() {
//		return status;
//	}
//
//	public void setStatus(StatusPedido status) {
//		this.status = status;
//	}
//
//	public User getCliente() {
//		return cliente;
//	}
//
//	public void setCliente(User cliente) {
//		this.cliente = cliente;
//	}
//
//	public Pagamento getPagamento() {
//		return pagamento;
//	}
//
//	public void setPagamento(Pagamento pagamento) {
//		this.pagamento = pagamento;
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		Pedido pedido = (Pedido) o;
//		return Objects.equals(id, pedido.id);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(id);
//	}
//}

package br.com.alexcosta.alexcosta.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter@NoArgsConstructor@AllArgsConstructor@EqualsAndHashCode(of="id")
@Entity
@Table(name = "tb_pedido")
public class Pedido implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Instant momento;

	private String numeroPedido;
	@Enumerated(EnumType.STRING)
	private StatusPedido status;

	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private User cliente;

		@OneToMany(mappedBy = "id.pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PedidoItem> items = new HashSet<>();

}