package br.com.aplrm.aplrm.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@Entity
@Table(name="tb_endereco")
public class Endereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Logradouro não pode estar vazio")
    private String logradouro;

    @NotBlank(message = "CEP não pode estar vazio")
   @Pattern(regexp = "\\d{8}", message = "CEP deve estar no formato 12345678")
    private String cep;
    
    @NotNull(message = "O número não pode ser nulo")
    @Min(value = 1, message = "O número deve ser pelo menos 1")
    @Max(value = 10000, message = "O número não pode ser maior que 10000")
    private Integer numero;

    @NotBlank(message = "Cidade não pode estar vazia")
    private String cidade;

    @NotBlank(message = "Bairro não pode estar vazio")
    private String bairro;


    private String complemento;

    @NotBlank(message = "UF não pode estar vazio")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String uf;




    public Endereco(Endereco endereco) {
        id= endereco.getId();
        logradouro = endereco.getLogradouro();
        cep = endereco.getCep();
        numero = endereco.getNumero();
        cidade = endereco.getCidade();
        bairro = endereco.getBairro();
        uf = endereco.getUf();
        complemento=endereco.getComplemento();
    }

    public Endereco( String logradouro, String cep, Integer numero, String cidade, String bairro, String complemento, String uf) {

        this.logradouro = logradouro;
        this.cep = cep;
        this.numero = numero;
        this.cidade = cidade;
        this.bairro = bairro;
        this.complemento = complemento;
        this.uf = uf;
    }
}
