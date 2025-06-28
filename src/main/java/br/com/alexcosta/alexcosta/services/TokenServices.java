package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.entities.Endereco;
import br.com.alexcosta.alexcosta.entities.Perfil;
import br.com.alexcosta.alexcosta.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenServices {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(User usuario) {
        validarSecret();

        try {
            Algorithm algoritmo = Algorithm.HMAC512(secret);



            return JWT.create()
                    .withIssuer("br.com.alexcosta")
                    .withSubject(String.valueOf(usuario.getId()))


                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        validarSecret();

        try {
            var algoritmo = Algorithm.HMAC512(secret);
            var decodedJWT = JWT.require(algoritmo)
                    .withIssuer("br.com.alexcosta")
                    .build()
                    .verify(tokenJWT);

            if (decodedJWT.getExpiresAt() != null && decodedJWT.getExpiresAt().getTime() < System.currentTimeMillis()) {
                throw new RuntimeException("Token expirado");
            }

            return decodedJWT.getSubject(); // Retorna o email do usuário (subject)
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }


    public boolean isValid(String tokenJWT) {
        validarSecret();

        try {
            var algoritmo = Algorithm.HMAC512(secret);
            JWT.require(algoritmo)
                    .withIssuer("br.com.alexcosta")
                    .build()
                    .verify(tokenJWT);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now()
                .plusMinutes(1440)
                .toInstant(ZoneOffset.of("-03:00"));
    }

    private void validarSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT Secret não configurado corretamente!");
        }
    }


}
