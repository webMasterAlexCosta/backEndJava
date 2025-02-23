package br.com.aplrm.aplrm.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Setter@Getter@NoArgsConstructor@AllArgsConstructor
@Entity
@Table(name="tb_tamanho")
public class Tamanho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descricao;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tamanho tamanho = (Tamanho) o;
        return Objects.equals(id, tamanho.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
