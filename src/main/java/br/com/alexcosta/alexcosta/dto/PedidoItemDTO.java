package br.com.alexcosta.alexcosta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItemDTO {

    private Integer produto;
    private Double preco;
    private Integer quantidade;
    private Integer tamanho;
    private String imgUrl;



    public Double getSubTotal(){
        return preco * quantidade;
    }
}
