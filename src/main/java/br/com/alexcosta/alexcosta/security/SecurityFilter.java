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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPublicRoute(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                String subject = tokenServices.getSubject(tokenJWT);
                var usuario = repository.findByEmail(subject);

                if (usuario != null) {
                    List<SimpleGrantedAuthority> authorities = usuario.getAuthorities().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                            .collect(Collectors.toList());

                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("Usuário autenticado: {} com authorities: {}", usuario.getUsername(), authorities);
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
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.matches("/produtos/\\d+") ||
                uri.startsWith("/produtos/paginas") ||
                uri.startsWith("/produtos/lista") ||
                uri.startsWith("/login/cliente3")||
                uri.startsWith("/login/cliente")||
                uri.startsWith("/produtos/buscar") ||
                uri.startsWith("/usuarios/cadastro") ||
                uri.startsWith("/publico") ||
                uri.startsWith("/api/recuperacao/solicitar") ||
                uri.startsWith("/codigocadastro/verificarcadastro");
    }

    private String recuperarToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7).trim())
                .orElse(null);
    }
}


//    private boolean isPublicRoute(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        return List.of(
//                "/produtos/paginas", "/produtos/lista", "/produtos/buscar", "/produtos/filtro",
//                "/usuarios/cadastro", "/codigocadastro/verificarcadastro", "/publico", "/api/recuperacao/solicitar"
//        ).stream().anyMatch(uri::startsWith) || uri.matches("/produtos/\\d+");
//    }

//    private String recuperarToken(HttpServletRequest request) {
//        var authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7);
//        }
//        return null;
//    }

