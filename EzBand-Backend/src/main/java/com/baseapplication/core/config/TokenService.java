package com.baseapplication.core.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String gerarToken(UserDetails userDetails){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(userDetails.getUsername())
                    .withExpiresAt(gerarDataDeExpiracao())
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException e){
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    public String validarToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return "";
        }
    }

    private Instant gerarDataDeExpiracao(){
        return LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
    }
}
