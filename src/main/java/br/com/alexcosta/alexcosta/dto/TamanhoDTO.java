package br.com.alexcosta.alexcosta.dto;

import br.com.alexcosta.alexcosta.entities.Tamanho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@NoArgsConstructor@AllArgsConstructor
public class TamanhoDTO {
    private int id;
    private String nome;

    public TamanhoDTO(Tamanho tamanho) {
        this.nome = tamanho.getDescricao();
        this.id = tamanho.getId();
    }
}
