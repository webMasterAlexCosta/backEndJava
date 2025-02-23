package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Tamanho;
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
