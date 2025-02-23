package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Endereco;
import br.com.aplrm.aplrm.entities.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Setter
@Getter
public class UserDTO {


    private UUID id;


    @Size(min = 3, max = 50, message = "O nome de usuario precisa ter entre 3 a 50 caracteres")
    private String nome;


    @Email(message = "Email deve ser válido")
    @Size(min = 10, max = 50, message = "O email precisa ter entre 10 a 50 caracteres")
    private String email;


    @Size(min = 11, max = 11, message = "O telefone precisa ter 11 caracteres")
    private String telefone;

    private LocalDate dataNascimento;



    @NotNull(message = "Endereco não pode ser nulo")
    private Endereco endereco;


    public UserDTO() {
    }

    public UserDTO(UUID id, String nome, String email, String telefone, LocalDate dataNascimento, Endereco endereco) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;

        this.endereco = endereco;
    }

    public UserDTO( User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.telefone = user.getTelefone();
        this.dataNascimento = user.getDataNascimento();
        this.endereco = user.getEndereco();
    }

    public UserDTO(Endereco endereco) {
        this.endereco = endereco;
    }
}
