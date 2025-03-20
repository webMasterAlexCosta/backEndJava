package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.controllers.handler.ControllerExceptionHandler;
import br.com.alexcosta.alexcosta.controllers.handler.ResourceNotFoundExceptions;
import br.com.alexcosta.alexcosta.dto.*;
import br.com.alexcosta.alexcosta.entities.Endereco;
import br.com.alexcosta.alexcosta.entities.Perfil;
import br.com.alexcosta.alexcosta.entities.User;
import br.com.alexcosta.alexcosta.entities.UserVerificador;
import br.com.alexcosta.alexcosta.repositories.EnderecoRepository;
import br.com.alexcosta.alexcosta.repositories.PerfilRepository;
import br.com.alexcosta.alexcosta.repositories.UserRepository;
import br.com.alexcosta.alexcosta.repositories.UserVerificadorRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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



    private Map<String, String> codigosDeRecuperacao = new HashMap<>();


    private final String urlServidor = "https://quaint-adele-alevivaldi-a5632bd1.koyeb.app/api/";
    private final String urlServicoEmail = "codigocadastro/verificarcadastro/";
 //   private final String urlServidor = "http://localhost:8080/api/";
    private final String urlServico = urlServidor + urlServicoEmail;

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
        // Validação do UUID
        if (uuid == null || uuid.length() != 36) {
            return "UUID inválido";
        }

        try {
            UUID uuidParsed = UUID.fromString(uuid);

            // Recupera o usuário verificando a existência do UUID
            Optional<UserVerificador> userVerificadorOptional = userVerificadorRepository.findByUuid(uuidParsed);

            if (userVerificadorOptional.isPresent()) {
                UserVerificador usuarioVerificacao = userVerificadorOptional.get();
                User user = usuarioVerificacao.getUser();
                Instant agora = Instant.now();

                // Verifica se a data de expiração não foi ultrapassada
                if (usuarioVerificacao.getDataExpiracao().isAfter(agora)) {
                    // Verifica a situação do usuário
                    if (user.isSituacao()) {
                        return "Usuário já cadastrado";
                    }

                    // Ativa o usuário e persiste no banco
                    user.setSituacao(true);
                    userRepository.save(user);

                    return "Usuário verificado. Por favor, retorne ao site.";
                } else {
                    // Remove o registro expirado e cria um novo
                    userVerificadorRepository.delete(usuarioVerificacao);

                    // Gera um novo UUID e uma nova data de expiração
                    UUID novoUuid = UUID.randomUUID();
                    Instant novaDataExpiracao = Instant.now().plus(2, ChronoUnit.MINUTES);

                    UserVerificador novoUserVerificador = new UserVerificador();
                    novoUserVerificador.setUuid(novoUuid);
                    novoUserVerificador.setDataExpiracao(novaDataExpiracao);
                    novoUserVerificador.setUser(user);

                    // Salva o novo link de verificação
                    userVerificadorRepository.save(novoUserVerificador);

                    if(user.isSituacao()) {
                        return "Usuário já cadastrado";
                    }
                    enviarNovoLink(user.getEmail(), novoUuid);

                    return "Tempo de verificação expirado. Um novo link foi enviado para o seu e-mail.";
                }
            } else {
                return "Usuário não encontrado";
            }
        } catch (IllegalArgumentException e) {
            return "UUID inválido";
        } catch (Exception e) {
            // Log de erro e resposta genérica
            logger.error("Erro ao verificar cadastro para UUID: {}", uuid, e);
            return "Erro interno. Por favor, tente novamente mais tarde.";
        }
    }


    //86400000
    @Scheduled(fixedRate = 3600000)
    public void LimparVerificadores() {
        Instant agora = Instant.now();
        List<UserVerificador> expirados = userVerificadorRepository.findByDataExpiracaoBefore(agora);
        userVerificadorRepository.deleteAll(expirados);
        List<User> inactiveUsers = userRepository.findBySituacaoFalse();
        userRepository.deleteAll(inactiveUsers);
    }

    @Transactional
    public UserCadastroDTO cadastro(@Valid UserCadastroDTO userCadastroDTO) {
        // Verificar se já existe um usuário com o CPF, telefone ou e-mail fornecido
        if (userRepository.existsByCpf(userCadastroDTO.getCpf())) {
            throw new ControllerExceptionHandler.CpfAlreadyExistsException("Já existe um usuário cadastrado com este CPF.");
        }

        if (userRepository.existsByTelefone(userCadastroDTO.getTelefone())) {
            throw new ControllerExceptionHandler.TelefoneAlreadyExistsException("Já existe um usuário cadastrado com este telefone.");
        }

        if (userRepository.existsByEmail(userCadastroDTO.getEmail())) {
            throw new ControllerExceptionHandler.EmailAlreadyExistsException("Já existe um usuário cadastrado com este e-mail.");
        }

        // Continuação do processo de cadastro...
        String encodedPassword = passwordEncoder.encode(userCadastroDTO.getSenha());
        User user = new User(userCadastroDTO);
        user.setSenha(encodedPassword);

        Endereco endereco = enderecoRepository.save(userCadastroDTO.getEndereco());
        user.setEndereco(endereco);

        Perfil userPerfil = perfilRepository.findByAuthority("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Perfil 'CLIENTE' não cadastrado"));
        user.getAuthorities().add(userPerfil);

        userRepository.save(user);

        // Criar e salvar o verificador de usuário
        UserVerificador userVerificador = criarUserVerificador(user);

        // Enviar email de boas-vindas
        enviarEmailBoasVindas(user, userVerificador);

        return new UserCadastroDTO(user);
    }


    @Transactional
    private void validarCadastro(UserCadastroDTO userCadastroDTO) {
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
    }

    private UserVerificador criarUserVerificador(User user) {
        UserVerificador userVerificador = new UserVerificador();
        userVerificador.setUser(user);
        userVerificador.setUuid(UUID.randomUUID());
        userVerificador.setDataExpiracao(Instant.now().plus(2 ,ChronoUnit.MINUTES));
        userVerificadorRepository.save(userVerificador);
        return userVerificador;
    }

    private void enviarEmailBoasVindas(User user, UserVerificador userVerificador) {
        String conteudoEmail = criarConteudoEmail(user, userVerificador);
        try {
            emailService.enviarEmailHtml(
                    user.getEmail(),
                    "Alex Costa ESPORT - Seja Bem-Vindo!!",
                    conteudoEmail
            );
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail de boas-vindas para o usuário: " + user.getEmail(), e);
            throw new RuntimeException("Erro ao enviar e-mail. Tente novamente.");
        }
    }

    private String criarConteudoEmail(User user, UserVerificador userVerificador) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; background-color: #f9f9f9; border-radius: 15px; max-width: 600px; margin: auto; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);'>" +
                "<header style='background: linear-gradient(135deg, #1a1a1a, #333333); padding: 20px; border-radius: 15px 15px 0 0; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);'>" +
                "<h1 style='color: white; margin: 0; font-size: 2.5em; font-family: Georgia, serif; animation: fadeIn 1s;'>Alex Costa Esports</h1>" +
                "</header>" +
                "<section style='padding: 20px;'>" +
                "<p style='font-size: 18px; color: #333; margin: 20px 0; line-height: 1.6;'>Olá <strong>" + user.getNome() + "</strong>, seja bem-vindo! Estamos empolgados por você fazer parte da nossa comunidade. Meu nome é Alex, e estou aqui para garantir que sua experiência seja incrível.</p>" +
                "<p style='font-size: 16px; color: #555; line-height: 1.5;'>Para ativar seu cadastro, clique no botão abaixo:</p>" +
                "<a href='" + urlServico + userVerificador.getUuid() + "' style='display: inline-block; padding: 15px 30px; background-color: #28a745; color: #fff; text-decoration: none; border-radius: 8px; font-weight: bold; transition: background-color 0.3s, transform 0.3s, box-shadow 0.3s; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);'>" +
                "Validar Cadastro" +
                "</a>" +
                "<p style='font-size: 14px; color: #777; margin-top: 15px; line-height: 1.4;'>Este link é válido por <strong>10 minutos</strong>. Caso não consiga acessá-lo dentro desse período, entre em contato conosco.</p>" +
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
                "</div>";
    }



    @Transactional
    private void enviarNovoLink(String email, UUID novoUuid) {

        try {
            // Gerar conteúdo HTML do e-mail
            String htmlContent = criarConteudoEmailDeRecuperacao(novoUuid);

            // Criar o MimeMessage
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);  // Não é mais necessário try-with-resources

            try {
                // Configurar e enviar o e-mail
                helper.setTo(email);
                helper.setSubject("Novo Link de Verificação");
                helper.setText(htmlContent, true);  // O conteúdo HTML gerado é passado para o helper
                emailSender.send(mimeMessage);  // Envia o e-mail

            } catch (MessagingException e) {
                logger.error("Erro ao criar ou enviar e-mail para: " + email, e);
                throw new RuntimeException("Erro ao enviar o e-mail de verificação. Tente novamente.");
            }

        } catch (Exception e) {
            logger.error("Erro ao preparar o e-mail de verificação para: " + email, e);
            throw new RuntimeException("Erro interno. Tente novamente.");
        }
    }



    private String criarConteudoEmailDeRecuperacao(UUID novoUuid) {
        String url = urlServico + novoUuid.toString();
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f0f0; display: flex; justify-content: center; align-items: center; height: 100vh; }" +
                ".container { max-width: 600px; width: 100%; padding: 20px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); text-align: center; }" +
                "header { background: linear-gradient(135deg, #050505, #dfe4e5); padding: 20px; border-radius: 10px 10px 0 0; }" +
                "h1 { color: white; margin: 0; font-size: 2.5em; }" +
                "p { font-size: 16px; color: #333; margin: 10px 0; }" +
                "a.button { display: inline-block; padding: 12px 24px; background-color: #28a745; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; transition: background-color 0.3s; margin-top: 20px; }" +
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
    }




    public User encontrarUsuarioPorEmailECpf(String email, String cpf) {
        return userRepository.findByEmailAndCpf(email, cpf);
    }

    public User encontrarUsuarioPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void atualizarSenhaRecuperada(UUID userId, String novaSenha) {
        // Valida se a nova senha possui pelo menos 8 caracteres
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + userId + " não encontrado"));

        try {
            String senhaCriptografada = passwordEncoder.encode(novaSenha);
            user.setSenha(senhaCriptografada);

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar a senha do usuário.", e);
        }
    }

    @Transactional
    public NovaSenhaDTO recuperarSenha(NovaSenhaDTO dto) {

        if (dto.getNovaSenha() == null || dto.getNovaSenha().isEmpty()) {
            throw new ControllerExceptionHandler.SenhaInvalidaException("A nova senha não pode ser vazia.");
        }

        User use = userRepository.findByEmail(dto.getEmail());
        if (use == null) {
            throw new ControllerExceptionHandler.SenhaInvalidaException("Usuário não encontrado.");
        }

        if (dto.getEmail().equals(use.getEmail()) && passwordEncoder.matches(dto.getAntigaSenha(), use.getSenha())) {
            String senhaCriptografada = passwordEncoder.encode(dto.getNovaSenha());
            use.setSenha(senhaCriptografada);

            userRepository.save(use);

            return new NovaSenhaDTO();
        } else {
            throw new ControllerExceptionHandler.SenhaInvalidaException("A senha atual é inválida");
        }
    }

    @Transactional
    public String updateFoto(UUID id, String foto) {
        // Recupera o usuário pelo id
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualiza a URL da foto
        user.setFoto(foto); // Supondo que você tenha um campo fotoPerfil no UserDTO e User

        // Salva a alteração no banco de dados
        userRepository.save(user);

        // Retorna uma mensagem ou algum valor indicativo de sucesso
        return "Foto atualizada com sucesso";
    }

    public String getFoto(UUID id) {
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    return user.getFoto();
    }

}





