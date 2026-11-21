package com.stable_angina.system.security;

import com.stable_angina.system.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JWT 签发与解析（jjwt 0.12.6）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties properties;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 令牌有效期
     */
    public Duration getTtl() {
        return Duration.ofMinutes(properties.getAccessTokenExpireMinutes());
    }

    /**
     * 签发令牌，并把 jti 回写到 LoginUser
     */
    public String createToken(LoginUser loginUser) {
        String jti = UUID.randomUUID().toString().replace("-", "");
        Instant now = Instant.now();
        Instant expire = now.plus(getTtl());
        String token = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(loginUser.getUserId()))
                .claim("username", loginUser.getUsername())
                .claim("roles", loginUser.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        loginUser.setJti(jti);
        return token;
    }

    /**
     * 解析并验签，失败返回 null
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        Object roles = claims.get("roles");
        return roles instanceof List<?> list ? (List<String>) list : List.of();
    }
}
