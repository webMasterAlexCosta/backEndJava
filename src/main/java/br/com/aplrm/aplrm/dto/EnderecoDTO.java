package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Endereco;
import br.com.aplrm.aplrm.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@NoArgsConstructor@AllArgsConstructor
public class EnderecoDTO {
    private Integer id;
    private String logradouro;
    private String cep;
    private Integer numero;
    private String cidade;
    private String bairro;
    private String complemento;

    private String uf;


    public EnderecoDTO(Endereco endereco) {
        this.id= endereco.getId();
        this.logradouro=endereco.getLogradouro();
        this.cep=endereco.getCep();
        this.numero=endereco.getNumero();
        this.cidade=endereco.getCidade();
        this.bairro= endereco.getBairro();
        this.complemento=endereco.getComplemento();
        this.uf=endereco.getUf();
    }
}
