package br.com.alexcosta.alexcosta.entities;

import br.com.alexcosta.alexcosta.dto.UserCadastroDTO;
import br.com.alexcosta.alexcosta.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails {


	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	private String nome;

	@Column(unique = true)
	@Email
	private String email;

	@Column(unique = true)
	private String telefone;

	private LocalDate dataNascimento;

	private String senha;
	@CPF
	private String cpf;


	private boolean situacao=false;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "endereco_id")
	private Endereco endereco;

	@OneToMany(mappedBy = "cliente")
	private List<Pedido> pedidos = new ArrayList<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_user_perfil",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "perfil_id"))
	@JsonManagedReference
	private Set<Perfil> authorities = new HashSet<>();


	public User(UserDTO user) {
		nome = user.getNome();
		email = user.getEmail();
		telefone = user.getTelefone();
		dataNascimento = user.getDataNascimento();
		endereco = user.getEndereco();
	}

	public User(UUID id, String nome, String email, String telefone, LocalDate dataNascimento, String senha, Endereco endereco) {
		this.id = UUID.randomUUID();
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
		this.dataNascimento = dataNascimento;
		this.senha = senha;
		this.endereco = endereco;

	}

	public User(UserCadastroDTO user) {
		nome = user.getNome();
		email = user.getEmail();
		telefone = user.getTelefone();
		dataNascimento = user.getDataNascimento();
		senha = user.getSenha();
		endereco = user.getEndereco();
		cpf=user.getCpf();
	}

	public User(UUID id, String nome, String email, String telefone, LocalDate dataNascimento, String senha, String cpf, Endereco endereco) {
		this.id =UUID.randomUUID();
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
		this.dataNascimento = dataNascimento;
		this.senha = senha;
		this.cpf = cpf;
		this.endereco = endereco;
	}

	public User() {
	}

	public UUID getId() {return id;	}


	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}


	public String getCpf() {
		return cpf;
	}

	public void setAuthorities(Set<Perfil> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}



	@Override
	public String getPassword() {
		return getSenha();
	}


	@Override
	public String getUsername() {
		return getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {


		return this.situacao;
	}

	@Override
	public Set<Perfil> getAuthorities() {
		return this.authorities;
	}

    public boolean isSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }


}
