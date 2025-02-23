package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Endereco;
import br.com.aplrm.aplrm.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.UUID;

public class UserCadastroDTO {



    @Getter
    private UUID id;

    @NotBlank
    @Size(min = 5, max = 50, message = "O nome de usuario precisa ter entre 5 a 50 caracteres")
    private String nome;

    @NotBlank
    @Email(message = "Email deve ser válido")
    @Size(min = 10, max = 50, message = "O email precisa ter entre 10 a 50 caracteres")
    private String email;

    @NotBlank
    @Size(min = 11, max = 11, message = "O telefone precisa ter 11 caracteres")
    private String telefone;

    @Getter
    private LocalDate dataNascimento;


    @Getter
    private String senha;

    @Getter
    @CPF
    private String cpf;

    @Getter
    @NotNull(message = "Endereco não pode ser nulo")
    private Endereco endereco;

    public UserCadastroDTO() {
    }

    public UserCadastroDTO(UUID id, String nome, String email, String telefone, LocalDate dataNascimento, String senha, String cpf, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public UserCadastroDTO(User user) {
        id = user.getId();
        nome = user.getNome();
        email = user.getEmail();
        telefone = user.getTelefone();
        dataNascimento = user.getDataNascimento();
        endereco = user.getEndereco();
        senha = user.getSenha();
        cpf = user.getCpf();
    }

    public UserCadastroDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public UserCadastroDTO(String senha) {
        this.senha = senha;
    }

    public @Size(min = 3, max = 50, message = "O nome de usuario precisa ter entre 3 a 50 caracteres") String getNome() {
        return nome;
    }

    public @Email(message = "Email deve ser válido") @Size(min = 10, max = 50, message = "O email precisa ter entre 10 a 50 caracteres") String getEmail() {
        return email;
    }

    public @Size(min = 11, max = 11, message = "O telefone precisa ter 11 caracteres") String getTelefone() {
        return telefone;
    }

}





