package com.stable_angina.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置，对应 application-{profile}.yml 的 sa.jwt.*
 */
@Data
@Component
@ConfigurationProperties(prefix = "sa.jwt")
public class JwtProperties {

    /** 签名密钥 */
    private String secret;

    /** 访问令牌有效期（分钟） */
    private long accessTokenExpireMinutes = 120;
}
