	package br.com.aplrm.aplrm.entities;

	import br.com.aplrm.aplrm.dto.ProdutoDTO;
	import com.fasterxml.jackson.annotation.*;
	import jakarta.persistence.*;
	import lombok.*;

	import java.io.Serializable;
	import java.util.HashSet;

	import java.util.Set;

	@Entity
	@Table(name="tb_produto")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(of="id")
	public class Produto implements Serializable {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;

		private String nome;
		private Double preco;

		@Column(columnDefinition = "TEXT")
		private String descricao;
		@Column(columnDefinition = "TEXT")
		private String imgUrl;

		private Integer quantidade;

		@ManyToMany
		@JoinTable(name = "tb_produto_tamanho",
				joinColumns = @JoinColumn(name = "produto_id"),
				inverseJoinColumns = @JoinColumn(name = "tamanho_id"))
		@JsonManagedReference
		private Set<Tamanho> tamanhos = new HashSet<>();

		@ManyToMany
		@JoinTable(name = "tb_produto_categoria",
				joinColumns = @JoinColumn(name = "produto_id"),
				inverseJoinColumns = @JoinColumn(name = "categoria_id"))
		@JsonManagedReference
		private Set<Categoria> categorias = new HashSet<>();

		@OneToMany(mappedBy = "id.produto")
		@JsonIgnore
		private Set<PedidoItem> items = new HashSet<>();


		public Produto(ProdutoDTO dto) {
			this.id = dto.getId();
			this.nome = dto.getNome();
			this.preco = dto.getPreco();
			this.descricao = dto.getDescricao();
			this.imgUrl = dto.getImgUrl();
		}
		public void removeCategoria(Categoria categoria) {
			this.categorias.remove(categoria);
		}
    }
