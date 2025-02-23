package br.com.aplrm.aplrm.dto;

import br.com.aplrm.aplrm.entities.Endereco;
import br.com.aplrm.aplrm.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor@AllArgsConstructor
public class UserPerfilDTO {

    @Getter
    private UUID id;

    @Size(min = 3, max = 50, message = "O nome de usuario precisa ter entre 3 a 50 caracteres")
    private String nome;

    @Email(message = "Email deve ser válido")
    @Size(min = 10, max = 50, message = "O email precisa ter entre 10 a 50 caracteres")
    private String email;

    @Size(min = 11, max = 11, message = "O telefone precisa ter 11 caracteres")
    private String telefone;

    @Getter
    private LocalDate dataNascimento;

    @Getter
    private List<String> perfil= new ArrayList<>();

   @Getter
   @NotNull(message = "Endereco não pode ser nulo")
    private Endereco endereco;

    public UserPerfilDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.telefone = user.getTelefone();
        this.dataNascimento = user.getDataNascimento();
        this.endereco = user.getEndereco();

        for(GrantedAuthority role: user.getAuthorities()){
            perfil.add(role.getAuthority());
        }
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
