package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.UserPerfilDTO;
import br.com.alexcosta.alexcosta.entities.User;
import br.com.alexcosta.alexcosta.dto.DadosAutenticacao;
import br.com.alexcosta.alexcosta.security.DadosTokenJWT;
import br.com.alexcosta.alexcosta.security.SecurityFilter;
import br.com.alexcosta.alexcosta.services.TokenBlacklistService;
import br.com.alexcosta.alexcosta.services.TokenServices;
import br.com.alexcosta.alexcosta.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/login")
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

    private final TokenBlacklistService blacklistService;

    public AutenticacaoController(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }


    @Autowired
    private SecurityFilter securityFilter;

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
    public ResponseEntity<DadosTokenJWT> efetuarLogin3(
            @RequestParam String email,
            @RequestParam String senha,
            HttpServletResponse response // <-- recebe isso também
    ) {
        var dados = new DadosAutenticacao(email, senha);

        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        var authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());

        if (!user.isSituacao()) {
            throw new RuntimeException("Usuário não ativou seu cadastro. Acesse seu e-mail e ative seu cadastro.");
        }

        String token = tokenServices.gerarToken(user);

        // CRIA O COOKIE
        Cookie cookie = new Cookie("TOKEN_KEY", token);
        cookie.setHttpOnly(true); // mais seguro, JS não acessa, mas front pode precisar em JS, então pode remover se necessário
        cookie.setSecure(true); // true se for HTTPS, senão false para dev
        cookie.setPath("/"); // acessível em todo o domínio
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias

        response.addCookie(cookie); // adiciona o cookie à resposta

        // Opcional: ainda retorna no body para compatibilidade
        return ResponseEntity.ok(new DadosTokenJWT(token));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = securityFilter.recuperarTokenControler(request);
        if (token != null) {
            blacklistService.addToBlacklist(token);
        }

        // Expira o cookie
        Cookie cookie = new Cookie("TOKEN_KEY", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expira imediatamente
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout realizado com sucesso");
    }





}

