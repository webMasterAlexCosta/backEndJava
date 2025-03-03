package br.com.aplrm.aplrm.services;

import br.com.aplrm.aplrm.entities.Endereco;
import br.com.aplrm.aplrm.entities.Perfil;
import br.com.aplrm.aplrm.entities.User;
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

            List<String> perfis = usuario.getAuthorities()
                    .stream()
                    .map(Perfil::getAuthority)
                    .toList();

            Map<String, Object> enderecoMap = new HashMap<>();
            if (usuario.getEndereco() != null) {
                Endereco endereco = usuario.getEndereco();
                enderecoMap.put("logradouro", endereco.getLogradouro());
                enderecoMap.put("numero", endereco.getNumero());
                enderecoMap.put("bairro", endereco.getBairro());
                enderecoMap.put("cidade", endereco.getCidade());
                enderecoMap.put("uf", endereco.getUf());
                enderecoMap.put("cep", endereco.getCep());
                enderecoMap.put("complemento", endereco.getComplemento());
            } else {
                enderecoMap.put("mensagem", "Endereço não cadastrado");
            }

            return JWT.create()
                    .withIssuer("aplm")
                   // .withSubject(usuario.getEmail())
                    .withClaim("perfis", perfis)
                    .withClaim("nome", usuario.getNome())
                    .withClaim("email", usuario.getEmail().toLowerCase())
                    .withClaim("endereco", enderecoMap)
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
            return JWT.require(algoritmo)
                    .withIssuer("aplm")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado");
        }
    }

    public boolean isValid(String tokenJWT) {
        validarSecret();

        try {
            var algoritmo = Algorithm.HMAC512(secret);
            JWT.require(algoritmo)
                    .withIssuer("aplm")
                    .build()
                    .verify(tokenJWT);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now()
                .plusMinutes(500) // Pode ser parametrizado futuramente
                .toInstant(ZoneOffset.of("-03:00"));
    }

    private void validarSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT Secret não configurado corretamente!");
        }
    }
}
