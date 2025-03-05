package br.com.alexcosta.alexcosta.dto;

import br.com.alexcosta.alexcosta.entities.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@NoArgsConstructor@AllArgsConstructor
public class CategoriaDTO {
    private Integer id;
    private String nome;

    public CategoriaDTO( Categoria categoria){}

}
