//package br.com.aplrm.aplrm.services;
//
//import br.com.aplrm.aplrm.entities.User;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTCreationException;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//
//@Service
//public class TokenServices {
//
//    @Value("${api.security.token.secret}")
//    private String secret;
//
//    public String gerarToken(User usuario){
//        try {
//            Algorithm algoritmo = Algorithm.HMAC512(secret);
//            return JWT.create()
//                    .withIssuer("aplm")
//                    .withSubject(usuario.getEmail())
//                    .withExpiresAt(dataExpiracao())
//                    .sign(algoritmo);
//        } catch (JWTCreationException exception){
//            throw new RuntimeException("erro ao gerar token jet",exception);        }
//    }
//    public String getSubject(String tokenJWT){
//        try {
//            var algoritmo = Algorithm.HMAC512(secret);
//            return JWT.require(algoritmo).withIssuer("aplm").build().verify(tokenJWT)
//                    .getSubject();
//        } catch (JWTVerificationException exception){
//            throw new RuntimeException("Token JWT invalido ou expirado");// Invalid signature/claims
//        }
//    }
//    private Instant dataExpiracao() {
//        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
//    }
//}


package br.com.aplrm.aplrm.services;

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

@Service
public class TokenServices {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(User usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC512(secret);
            return JWT.create()
                    .withIssuer("aplm")
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }
    public String getSubject(String tokenJWT) {
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
        return LocalDateTime.now().plusMinutes(500).toInstant(ZoneOffset.of("-03:00"));
    }
}
