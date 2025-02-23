package br.com.aplrm.aplrm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter@NoArgsConstructor
public class UpdateSenha {
    private UUID id;
    private String senhaAntiga;
    private String senhaNova;

    public UpdateSenha(UUID id, String senhaAntiga, String senhaNova) {
        this.id = id;
        this.senhaAntiga = senhaAntiga;
        this.senhaNova = senhaNova;
    }
}
