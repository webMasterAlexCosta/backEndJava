package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.dto.*;
import br.com.alexcosta.alexcosta.entities.Endereco;
import br.com.alexcosta.alexcosta.entities.Perfil;
import br.com.alexcosta.alexcosta.entities.User;
import br.com.alexcosta.alexcosta.entities.UserVerificador;
import br.com.alexcosta.alexcosta.repositories.EnderecoRepository;
import br.com.alexcosta.alexcosta.repositories.PerfilRepository;
import br.com.alexcosta.alexcosta.repositories.UserRepository;
import br.com.alexcosta.alexcosta.repositories.UserVerificadorRepository;
import br.com.alexcosta.alexcosta.controllers.handler.ResourceNotFoundExceptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private PerfilService perfilService;
    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserVerificadorRepository userVerificadorRepository;
    @Autowired
    private JavaMailSender emailSender;

    public Map<String, String> codigosDeRecuperacao = new HashMap<>();
   


    @Transactional
    public void delete(UUID id) {
        try {
            User user = userRepository.getReferenceById(id);
            userRepository.delete(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Usuário não encontrado para Exclusão");
        }
    }

    public UserDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        return new UserDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserCadastroDTO> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserCadastroDTO::new);
    }

    @Transactional
    public UserDTO insert(UserDTO dto) {
        User user = new User(dto);
        user.setEndereco(enderecoRepository.save(dto.getEndereco()));
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO dto) {
        try {
            User user = userRepository.getReferenceById(id);
            user.setNome(dto.getNome());
            user.setEmail(dto.getEmail());
            user.setTelefone(dto.getTelefone());
            user.setDataNascimento(dto.getDataNascimento());

            Endereco endereco = user.getEndereco();
            endereco.setLogradouro(dto.getEndereco().getLogradouro());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setUf(dto.getEndereco().getUf());
            endereco.setNumero(dto.getEndereco().getNumero());

            user = userRepository.save(user);
            return new UserDTO(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Usuário não encontrado para atualização");
        }
    }

    @Transactional
    public void alterarSenha(UUID id, UpdateSenha dto) {
        logger.info("Atualizando senha para o usuário com ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Usuário não encontrado"));

        if (passwordEncoder.matches(dto.getSenhaAntiga(), user.getSenha())) {
            user.setSenha(passwordEncoder.encode(dto.getSenhaNova()));
            userRepository.save(user);
            logger.info("Senha atualizada com sucesso para o usuário com ID: {}", id);
        } else {
            throw new ResourceNotFoundExceptions("Erro ao atualizar a senha do usuário: senha antiga incorreta");
        }
    }

    @Transactional
    public UserDTO alterarEmail(UUID id, @Valid UserDTO dto) {
        try {
            User user = userRepository.getReferenceById(id);
            user.setEmail(dto.getEmail());
            userRepository.save(user);
            return new UserDTO(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Usuário não encontrado para atualização");
        }
    }

    @Transactional
    public EnderecoDTO alterarEndereco(UUID id, @Valid EnderecoDTO dto) {
        try {
            User user = userRepository.getReferenceById(id);
            Endereco endereco = user.getEndereco();
            endereco.setLogradouro(dto.getLogradouro());
            endereco.setCep(dto.getCep());
            endereco.setNumero(dto.getNumero());
            endereco.setCidade(dto.getCidade());
            endereco.setBairro(dto.getBairro());
            endereco.setUf(dto.getUf());
            endereco.setComplemento(dto.getComplemento());
            userRepository.save(user);
            return new EnderecoDTO(endereco);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Usuário não encontrado para atualização");
        }
    }

    @Transactional
    public UserDTO alterarDataNascimento(UUID id, @Valid UserDTO dto) {
        try {
            User user = userRepository.getReferenceById(id);
            user.setDataNascimento(dto.getDataNascimento());
            userRepository.save(user);
            return new UserDTO(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Usuário não encontrado para atualização");
        }
    }

    @Transactional(readOnly = true)
    protected User authenticated() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Usuário autenticado: " + username);

            if (username == null || username.isEmpty()) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }

            User user = userRepository.findByEmail(username);
            System.out.println("Usuário encontrado no banco: " + user);

            return user;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário Inválido", e);
        }
    }


    @Transactional(readOnly = true)
    public UserPerfilDTO getMe() {
        User user = authenticated();
        System.out.println(user);
        return new UserPerfilDTO(user);
    }

    public String verificarCadastro(String uuid) {
        if (uuid == null || uuid.length() != 36) {
            return "UUID inválido";
        }

        Optional<UserVerificador> userVerificadorOptional = userVerificadorRepository.findByUuid(UUID.fromString(uuid));

        if (userVerificadorOptional.isPresent()) {
            UserVerificador usuarioVerificacao = userVerificadorOptional.get();
            Instant agora = Instant.now();

            if (usuarioVerificacao.getDataExpiracao().isAfter(agora)) {
                User user = usuarioVerificacao.getUser();

                if (user.isSituacao()) {
                    //userVerificadorRepository.delete(usuarioVerificacao);
                    return "Usuário já cadastrado";
                }
                user.setSituacao(true);
                userRepository.save(user);
                //userVerificadorRepository.delete(usuarioVerificacao);
                return "Usuário verificado. Por favor, retorne ao site.";
            } else {
                userVerificadorRepository.delete(usuarioVerificacao);

                UUID novoUuid = UUID.randomUUID();
                Instant novaDataExpiracao = Instant.now().plus(6000, ChronoUnit.SECONDS);

                UserVerificador novoUserVerificador = new UserVerificador();
                novoUserVerificador.setUuid(novoUuid);
                novoUserVerificador.setDataExpiracao(novaDataExpiracao);
                novoUserVerificador.setUser(usuarioVerificacao.getUser());

                userVerificadorRepository.save(novoUserVerificador);

                enviarNovoLink(usuarioVerificacao.getUser().getEmail(), novoUuid);

                return "Tempo de verificação expirado. Um novo link foi enviado para o seu e-mail.";
            }
        } else {
            return "Usuário não encontrado";
        }
    }

    @Scheduled(fixedRate = 900000)
    public void LimparVerificadores() {
        Instant agora = Instant.now();
        List<UserVerificador> expirados = userVerificadorRepository.findByDataExpiracaoBefore(agora);
        userVerificadorRepository.deleteAll(expirados);
        List<User> inactiveUsers = userRepository.findBySituacaoFalse();
        userRepository.deleteAll(inactiveUsers);
    }

    @Transactional
    public UserCadastroDTO Cadastro(@Valid UserCadastroDTO userCadastroDTO) {
        if (userCadastroDTO.getSenha() == null || userCadastroDTO.getSenha().length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (userRepository.existsByEmail(userCadastroDTO.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este E-mail.");
        }
        if (userRepository.existsByCpf(userCadastroDTO.getCpf())) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF.");
        }
        if (userRepository.existsByTelefone(userCadastroDTO.getTelefone())) {
            throw new IllegalArgumentException("Já existe um usuário com este Telefone.");
        }

        String encodedPassword = passwordEncoder.encode(userCadastroDTO.getSenha());
        User user = new User(userCadastroDTO);
        user.setSenha(encodedPassword);

        Endereco endereco = enderecoRepository.save(userCadastroDTO.getEndereco());
        user.setEndereco(endereco);

        Perfil userPerfil = perfilRepository.findByAuthority("CLIENT")
                .orElseThrow(() -> new RuntimeException("Perfil não cadastrado"));
        user.getAuthorities().add(userPerfil);

        userRepository.save(user);
        UserVerificador userVerificador = new UserVerificador();
        userVerificador.setUser(user);
        userVerificador.setUuid(UUID.randomUUID());
        userVerificador.setDataExpiracao(Instant.now().plusMillis(3600000));
        userVerificadorRepository.save(userVerificador);

        String urlServidor = "https://quaint-adele-alevivaldi-a5632bd1.koyeb.app/";

       // String urlLocalHost="http://localhost:8080/";
        String urlDominio = urlServidor;

        String urlServicoEmail = "codigocadastro/verificarcadastro/";

        String url =urlDominio+urlServicoEmail ;

        emailService.enviarEmailHtml(
                user.getEmail(),
                "Alex Costa ESPORT - Seja Bem-Vindo!!",
                "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; background-color: #f9f9f9; border-radius: 15px; max-width: 600px; margin: auto; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);'>" +
                        "<header style='background: linear-gradient(135deg, #1a1a1a, #333333); padding: 20px; border-radius: 15px 15px 0 0; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);'>" +
                        "<h1 style='color: white; margin: 0; font-size: 2.5em; font-family: Georgia, serif; animation: fadeIn 1s;'>Alex Costa Esports</h1>" +
                        "</header>" +
                        "<section style='padding: 20px;'>" +
                        "<p style='font-size: 18px; color: #333; margin: 20px 0; line-height: 1.6;'>Olá <strong>" + user.getNome() + "</strong>, seja bem-vindo! Estamos empolgados por você fazer parte da nossa comunidade. Meu nome é Alex, e estou aqui para garantir que sua experiência seja incrível.</p>" +
                        "<p style='font-size: 16px; color: #555; line-height: 1.5;'>Para ativar seu cadastro, clique no botão abaixo:</p>" +
                        "<a href='" + url + userVerificador.getUuid() + "' style='display: inline-block; padding: 15px 30px; background-color: #28a745; color: #fff; text-decoration: none; border-radius: 8px; font-weight: bold; transition: background-color 0.3s, transform 0.3s, box-shadow 0.3s; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);'>" +
                        "Validar Cadastro" +
                        "</a>" +
                        "<p style='font-size: 14px; color: #777; margin-top: 15px; line-height: 1.4;'>Este link é válido por <strong>60 minutos</strong>. Caso não consiga acessá-lo dentro desse período, entre em contato conosco.</p>" +
                        "</section>" +
                        "<footer style='margin-top: 20px; padding: 15px; background-color: #f0f0f0; border-radius: 0 0 15px 15px; color: #999; font-size: 12px; line-height: 1.4;'>" +
                        "© 2025 Alex Costa Esports. Todos os direitos reservados.<br>" +
                        "Caso tenha dúvidas, entre em contato através do email <a href='mailto:thg6321@gmail.com' style='color: #28a745; text-decoration: none;'>suporte@alexcostaesports.com</a>." +
                        "</footer>" +
                        "<style>" +
                        "@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }" +
                        "a:hover { background-color: #218838; transform: translateY(-2px); box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); }" +
                        "@media (max-width: 600px) {" +
                        "h1 { font-size: 2em; }" +
                        "p { font-size: 16px; }" +
                        "a { padding: 12px 24px; }" +
                        "}" +
                        "</style>" +
                        "</div>"
        );
        return new UserCadastroDTO(user);
    }

    @Transactional
    private void enviarNovoLink(String email, UUID novoUuid) {
        String url = "https://exceptional-cathi-alevivaldi-fe38a61b.koyeb.app/codigocadastro/verificarcadastro/" + novoUuid.toString();
        String htmlContent = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f0f0; }" +
                ".container { max-width: 600px; margin: auto; padding: 20px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }" +
                "header { background: linear-gradient(135deg, #050505, #dfe4e5); padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }" +
                "h1 { color: white; margin: 0; font-size: 2.5em; }" +
                "p { font-size: 16px; color: #333; }" +
                "a.button { display: inline-block; padding: 12px 24px; background-color: #28a745; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; transition: background-color 0.3s; }" +
                "a.button:hover { background-color: #218838; }" +
                "footer { margin-top: 20px; text-align: center; color: #999; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<header>" +
                "<h1>Alex Costa Esports</h1>" +
                "</header>" +
                "<p>Olá!</p>" +
                "<p>Seu link de verificação expirou.</p>" +
                "<p>Clique no seguinte link para verificar sua conta:</p>" +
                "<a href=\"" + url + "\" class=\"button\">Clique aqui para validar</a>" +
                "<p style='font-size: 14px; color: #777; margin-top: 10px;'>Link válido por 10 minutos.</p>" +
                "<footer>© 2025 Alex Costa Esports. Todos os direitos reservados.</footer>" +
                "</div>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        try {
            helper.setTo(email);
            helper.setSubject("Novo Link de Verificação");
            helper.setText(htmlContent, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public User encontrarUsuarioPorEmailECpf(String email, String cpf) {
        return userRepository.findByEmailAndCpf(email, cpf);
    }

    public User encontrarUsuarioPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void atualizarSenhaRecuperada(UUID userId, String novaSenha) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Criptografa a nova senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        user.setSenha(senhaCriptografada);

        userRepository.save(user);
    }

    public void recuperarSenha(String codigo, String novaSenha) {
        String email = codigosDeRecuperacao.get(codigo);
        if (email != null) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                String senhaCriptografada = passwordEncoder.encode(novaSenha);
                user.setSenha(senhaCriptografada);
                userRepository.save(user);
            }
        }
    }
}





