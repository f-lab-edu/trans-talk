package flab.transtalk.auth.security.jwt;
import flab.transtalk.auth.exception.JwtAuthenticationException;
import flab.transtalk.auth.exception.message.JwtExceptionMessages;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    private void init() {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String subject, String role) {
        return createToken(subject, Map.of("role", role));
    }

    public String createToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(
                    "TOKEN_EXPIRED",
                    JwtExceptionMessages.TOKEN_EXPIRED, e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(
                    "INVALID_SIGNATURE",
                    JwtExceptionMessages.INVALID_SIGNATURE, e);
        }
    }
}
