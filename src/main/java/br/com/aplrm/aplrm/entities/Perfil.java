package br.com.aplrm.aplrm.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;

@Setter
@Entity
@Table(name="tb_perfil")
public class Perfil implements GrantedAuthority{
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String authority;

    public Perfil(){}

    public Perfil(Integer id, String authority) {
        super();
        this.id = id;
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
