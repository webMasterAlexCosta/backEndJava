package br.com.alexcosta.alexcosta.security;

import br.com.alexcosta.alexcosta.repositories.UserRepository;
import br.com.alexcosta.alexcosta.services.TokenServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Ignorar o filtro de autenticação para requisições públicas (operacoes de leitura dos produtos)
        if (isPublicRoute(request)) {
            filterChain.doFilter(request, response); // Permite o acesso sem autenticação
            return;
        }

        // Caso contrário, faz a verificação do token JWT
        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                // Recupera o subject (email) do token
                String subject = tokenServices.getSubject(tokenJWT);
                var usuario = repository.findByEmail(subject);

                if (usuario != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuário autenticado: {} com authorities: {}", usuario.getUsername(), usuario.getAuthorities());
                    return;
                } else {
                    logger.warn("Usuário não encontrado: {}", subject);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
                    return;
                }
            } catch (Exception e) {
                logger.error("Erro ao autenticar o token: ", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro na autenticação do token");
                return;
            }
        } else {
            logger.warn("Token não encontrado no cabeçalho");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token não encontrado");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // Lista de rotas públicas que não precisam de autenticação
        return uri.startsWith("/produtos/paginas") || // Página de produtos
                uri.matches("/produtos/\\d+") ||
                uri.matches("/produtos/lista") ||
                uri.matches("/produtos/filtro")||

                // Produto com ID específico (ex: /produtos/1)
                uri.startsWith("/login/cliente3"); // Login sem autenticação
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
