package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.NovaSenhaDTO;
import br.com.alexcosta.alexcosta.dto.RecuperacaoSenhaDTO;

import br.com.alexcosta.alexcosta.entities.User;
import br.com.alexcosta.alexcosta.services.EmailService;
import br.com.alexcosta.alexcosta.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recuperacao")
public class PasswordRecoveryController {


    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody RecuperacaoSenhaDTO dto) {
        User user = userService.encontrarUsuarioPorEmailECpf(dto.getEmail(), dto.getCpf());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        String novaSenha = "B&D - SISTEMA " + UUID.randomUUID().toString().replace("-", "").substring(0, 10)
        +"ALEXCOSTA"+
                UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        userService.atualizarSenhaRecuperada(user.getId(), novaSenha);

        // Enviar email com a nova senha
        String mensagemHtml = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }" +
                ".container { max-width: 600px; margin: auto; padding: 20px; background-color: #ffffff; border-radius: 15px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }" +
                "header { background: linear-gradient(135deg, #1a1a1a, #333333); padding: 20px; border-radius: 15px 15px 0 0; text-align: center; }" +
                "h1 { color: white; margin: 0; font-size: 2.5em; font-family: Georgia, serif; animation: fadeIn 1s; }" +
                "p { font-size: 16px; color: #333; line-height: 1.6; margin: 15px 0; }" +
                ".senha-box { background-color: #f0f0f0; padding: 15px; border-radius: 8px; display: inline-block; margin: 20px 0; font-size: 18px; font-weight: bold; color: #28a745; }" +
                "footer { margin-top: 20px; text-align: center; color: #999; font-size: 14px; line-height: 1.4; }" +
                "@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }" +
                "@media (max-width: 600px) {" +
                "h1 { font-size: 2em; }" +
                "p { font-size: 14px; }" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<header>" +
                "<h1>Recuperação de Senha</h1>" +
                "</header>" +
                "<p>Olá <strong>" + user.getNome() + "</strong>,</p>" +
                "<p>Sua nova senha é:</p>" +
                "<div class='senha-box'>" + novaSenha + "</div>" +
                "<p>Recomendamos que você altere esta senha após fazer login por razões de segurança.</p>" +
                "<footer>© 2025 Alex Costa Esports. Todos os direitos reservados.</footer>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.enviarEmailHtml(
                user.getEmail(),
                "Nova Senha",
                mensagemHtml
        );
        ;

        return ResponseEntity.ok("Email enviado para: " + user.getEmail() + " com a nova senha.");
    }

    @PostMapping("/alterar")
    public ResponseEntity<String> recuperarSenha(@RequestBody NovaSenhaDTO dto) {
        NovaSenhaDTO  novaSenha = userService.recuperarSenha(dto);

        return ResponseEntity.ok("Senha alterada com sucesso.");

    }




}
