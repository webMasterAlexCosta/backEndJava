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

//            List<String> perfis = usuario.getAuthorities()
//                    .stream()
//                    .map(Perfil::getAuthority)
//                    .toList();
//
//            Map<String, Object> enderecoMap = new HashMap<>();
//            if (usuario.getEndereco() != null) {
//                Endereco endereco = usuario.getEndereco();
//                enderecoMap.put("logradouro", endereco.getLogradouro());
//                enderecoMap.put("numero", endereco.getNumero());
//                enderecoMap.put("bairro", endereco.getBairro());
//                enderecoMap.put("cidade", endereco.getCidade());
//                enderecoMap.put("uf", endereco.getUf());
//                enderecoMap.put("cep", endereco.getCep());
//                enderecoMap.put("complemento", endereco.getComplemento());
//            } else {
//                enderecoMap.put("mensagem", "Endereço não cadastrado");
//            }

            return JWT.create()
                    .withIssuer("alexCosta")
                    .withSubject(String.valueOf(usuario.getId()))
//                    .withClaim("perfis", perfis)
//                    .withClaim("nome", usuario.getNome())
//                    .withClaim("email", usuario.getEmail().toLowerCase())
//                    .withClaim("endereco", enderecoMap)
//                    .withClaim("telefone", usuario.getTelefone())
//                    .withClaim("dataNascimento", String.valueOf(usuario.getDataNascimento()))
//                    .withClaim("id", String.valueOf(usuario.getId()))


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
                    .withIssuer("alexCosta")
                    .build()
                    .verify(tokenJWT);

            // Verificando se a data de expiração é null antes de comparar
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
                    .withIssuer("alexcosta")
                    .build()
                    .verify(tokenJWT);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now()
                .plusMinutes(60) // Pode ser parametrizado futuramente
                .toInstant(ZoneOffset.of("-03:00"));
    }

    private void validarSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT Secret não configurado corretamente!");
        }
    }


}
