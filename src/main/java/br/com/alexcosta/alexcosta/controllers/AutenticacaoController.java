package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.UserPerfilDTO;
import br.com.alexcosta.alexcosta.entities.User;
import br.com.alexcosta.alexcosta.dto.DadosAutenticacao;
import br.com.alexcosta.alexcosta.security.DadosTokenJWT;
import br.com.alexcosta.alexcosta.services.TokenServices;
import br.com.alexcosta.alexcosta.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacaoController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private UserService userService;

    @PostMapping("/cliente")
    public ResponseEntity<Map<String, Object>> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {

        try {
            if (dados.email() == null || dados.senha() == null) {
                throw new IllegalArgumentException("Email nao pode ser nulo");
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    dados.email(), dados.senha()
            );
            var authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            User user = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());
            if(!user.isSituacao()){
                throw new RuntimeException("Usuario não ativou seu Cadastro, Acesse seu email e ative seu cadastro");
            }
            String token = tokenServices.gerarToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);

            response.put("user", new UserPerfilDTO(user));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid input: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/autenticacao/validarToken")
    public ResponseEntity<String> validarToken(HttpServletRequest request) {
        System.out.println("testando autenticacao");
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            boolean isValid = tokenServices.isValid(token.substring(7));
            return isValid ? ResponseEntity.ok("Token válido") : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido");
        }
        return ResponseEntity.badRequest().body("Token não fornecido");    }


    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> efetuarLoginAdmin(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            if (dados.email() == null || dados.senha() == null) {
                throw new IllegalArgumentException("Email ou password nao pode ser nullo");
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    dados.email(), dados.senha()
            );

            var authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());

            boolean verificar = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("CLIENT"));

            if (verificar) {
                throw new RuntimeException("Usuário não autorizado");
            }

            String token = tokenServices.gerarToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", new UserPerfilDTO(user));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid input: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
//aqui o formato é JSON
       @PostMapping("/cliente2")
   public ResponseEntity<DadosTokenJWT> efetuarLogin2(@RequestBody @Valid DadosAutenticacao dados) {
       var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
       var authentication = authenticationManager.authenticate(authenticationToken);
       UserDetails user = (UserDetails) authentication.getPrincipal();
       String token = tokenServices.gerarToken((User) user);
       return ResponseEntity.ok(new DadosTokenJWT(token));
   }
   //aqui o formato é x-www-form
   @PostMapping("/cliente3")
   public ResponseEntity<DadosTokenJWT> efetuarLogin3(@RequestParam DadosAutenticacao email, @RequestParam DadosAutenticacao senha) {
       // Criação dos dados de autenticação com email e senha
       var dados = new DadosAutenticacao(email.email(), senha.senha());

       // Criação do token de autenticação com as credenciais do usuário
       var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

       // Autenticação do usuário
       var authentication = authenticationManager.authenticate(authenticationToken);

       // Obtenção do usuário autenticado
       UserDetails userDetails = (UserDetails) authentication.getPrincipal();

       // Carrega o usuário do banco de dados usando o serviço de UserDetails
       User user = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());

       // Verifica se o usuário está ativo
       if (!user.isSituacao()) {
           throw new RuntimeException("Usuário não ativou seu cadastro. Acesse seu e-mail e ative seu cadastro.");
       }

       // Geração do token JWT
       String token = tokenServices.gerarToken(user);

       // Retorna a resposta com o token gerado
       return ResponseEntity.ok(new DadosTokenJWT(token));
   }


//    @GetMapping(value="/verificarCadastro/{uuid}")
//    public String verificarCadastro(@PathVariable("uuid") String uuid){
//       return userService.verificarCadastro(uuid);
//    }


}

