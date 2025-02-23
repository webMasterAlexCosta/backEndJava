package br.com.aplrm.aplrm.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NovaSenhaDTO {

    // Getters e Setters
    @NotBlank(message = "Código é obrigatório.")
    private String codigo;

    @NotBlank(message = "Nova senha é obrigatória.")
    private String novaSenha;

}
