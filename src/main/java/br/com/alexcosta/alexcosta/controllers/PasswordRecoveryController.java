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

        String novaSenha = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        userService.atualizarSenhaRecuperada(user.getId(), novaSenha);

        // Enviar email com a nova senha
        String mensagemHtml = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f0f0; }" +
                ".container { max-width: 600px; margin: auto; padding: 20px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }" +
                "header { background: linear-gradient(135deg, #050505, #dfe4e5); padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }" +
                "h1 { color: white; margin: 0; font-size: 2.5em; }" +
                "p { font-size: 16px; color: #333; }" +
                "footer { margin-top: 20px; text-align: center; color: #999; font-size: 14px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<header>" +
                "<h1>Recuperação de Senha</h1>" +
                "</header>" +
                "<p>Olá!</p>" +user.getNome()+
                "<p> Sua nova senha é: <strong>" + novaSenha + "</strong></p>" +
                "<p>Recomendamos que você a altere após fazer login.</p>" +
                "<footer>© 2024 VPC Esports. Todos os direitos reservados.</footer>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.enviarEmailHtml(
                user.getEmail(),
                "Nova Senha",
                mensagemHtml
        );
        ;

        return ResponseEntity.ok("Email enviado com a nova senha.");
    }

    @PostMapping("/alterar")
    public ResponseEntity<String> recuperarSenha(@RequestBody NovaSenhaDTO dto, @RequestParam String codigo) {
        String email = userService.codigosDeRecuperacao.get(codigo);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido ou expirado.");
        }

        User user = userService.encontrarUsuarioPorEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        userService.recuperarSenha(codigo, dto.getNovaSenha());

        emailService.enviarEmailHtml(
                user.getEmail(),
                "Senha Alterada",
                "Sua senha foi alterada com sucesso."
        );

        return ResponseEntity.ok("Senha alterada com sucesso.");

    }




}
