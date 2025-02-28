package com.example.taskmanagement.jwt;

import com.example.taskmanagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTUtils {

    private static final String SECRET_KEY = "SecretKey1234567890hbhjdbfjhbfhjbfosdhfbo48rhiufnbcdsuh834urfbjhrbnvf";
    private static final long EXPIRATION_TIME = 86400000;
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public boolean isTokenValid(String token) {
        try {

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            Claims tokenClaims = claims.getBody();

            Date expirationDate = tokenClaims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                return false;
            }
            log.info("Token is Valid " + extractAllClaims(token).getSubject());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}