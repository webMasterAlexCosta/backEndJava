package br.com.aplrm.aplrm.entities.usuario;

import br.com.aplrm.aplrm.entities.User;
import br.com.aplrm.aplrm.repositories.UserRepository;
import br.com.aplrm.aplrm.services.TokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usuarioRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public String realizarLogin(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email ou senha não pode ser nulo");
        }

        User user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(password, user.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return tokenServices.gerarToken(user);
    }

    @Transactional
    public User buscarPerfil(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email ou senha não pode ser nulo");
        }

        User user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(password, user.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return user;
    }
}
