package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Categoria;
import br.com.aplrm.aplrm.entities.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

@Getter@NoArgsConstructor@AllArgsConstructor
public class ProdutoDTO {

    private Integer id;

    @NotBlank(message = "O Campo é Obrigatório e não pode ser Vazio!")
    @Size(min = 3, max = 80, message = "Nome precisa ter entre 3 a 80 caracteres")
    private String nome;

    @Positive(message = "O preço deve ser positivo")
    private Double preco;

    @NotBlank(message = "O Campo é Obrigatório e não pode ser Vazio!")
    @Size(min = 8, max = 800, message = "A descrição precisa ter entre 8 a 800 caracteres")
    private String descricao;

    @NotBlank
    private String imgUrl;

    private Integer quantidade;

    private Set<TamanhoDTO> tamanhos;

    private Set<CategoriaDTO> categorias;

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.preco = produto.getPreco();
        this.descricao = produto.getDescricao();
        this.imgUrl = produto.getImgUrl();

        this.tamanhos = produto.getTamanhos().stream()
                .map(tamanho -> new TamanhoDTO(tamanho.getId(), tamanho.getDescricao()))
                .collect(Collectors.toSet());

        this.categorias = produto.getCategorias().stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome()))
                .collect(Collectors.toSet());
    }
}
