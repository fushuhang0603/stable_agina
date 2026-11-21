package com.stable_angina.common.constant;

/**
 * 认证鉴权常量
 */
public final class AuthConstants {

    private AuthConstants() {
    }

    /** 超级管理员角色标识 */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    /** 全部权限通配符 */
    public static final String PERMISSION_ALL = "*";
    /** 令牌请求头 */
    public static final String AUTH_HEADER = "Authorization";
    /** 令牌前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";
    /** Redis 令牌白名单前缀 */
    public static final String TOKEN_CACHE_PREFIX = "auth:token:";
}
