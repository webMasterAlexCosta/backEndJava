package br.com.alexcosta.alexcosta.dto;

import br.com.alexcosta.alexcosta.entities.User;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecuperacaoSenhaDTO {
    private UUID id;
    private String email;
    private String cpf;


    public RecuperacaoSenhaDTO(User user) {
        this.email= user.getEmail();
        this.cpf= user.getCpf();
    }
}
