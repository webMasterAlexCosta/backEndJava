package br.com.alexcosta.alexcosta.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NovaSenhaDTO {

    // Getters e Setters
    @NotBlank(message = "Senha Antiga Obrigatorio")
    private String antigaSenha;

    @NotBlank(message = "Nova senha é obrigatória.")
    private String novaSenha;

    @NotBlank
    @Email
    String email;
}
