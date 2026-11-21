package com.stable_angina.system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stable_angina.common.constant.AuthConstants;
import com.stable_angina.system.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 令牌白名单：auth:token:{userId}:{jti} → LoginUser JSON，TTL 与令牌一致
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenStore {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    private String key(Long userId, String jti) {
        return AuthConstants.TOKEN_CACHE_PREFIX + userId + ":" + jti;
    }

    public void save(LoginUser loginUser) {
        try {
            String json = objectMapper.writeValueAsString(loginUser);
            redisTemplate.opsForValue().set(key(loginUser.getUserId(), loginUser.getJti()), json, ttl());
        } catch (Exception e) {
            throw new IllegalStateException("写入令牌白名单失败", e);
        }
    }

    public LoginUser load(Long userId, String jti) {
        String json = redisTemplate.opsForValue().get(key(userId, jti));
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, LoginUser.class);
        } catch (Exception e) {
            log.warn("反序列化登录信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 滑动续期
     */
    public void renew(Long userId, String jti) {
        redisTemplate.expire(key(userId, jti), ttl());
    }

    public void remove(Long userId, String jti) {
        redisTemplate.delete(key(userId, jti));
    }

    private Duration ttl() {
        return Duration.ofMinutes(jwtProperties.getAccessTokenExpireMinutes());
    }
}
