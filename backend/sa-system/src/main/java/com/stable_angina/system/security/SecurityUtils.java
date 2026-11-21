package com.stable_angina.system.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户上下文：SecurityContextHolder 默认即基于 ThreadLocal 存储
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 当前登录用户，未登录返回 null
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser
                ? loginUser : null;
    }

    /**
     * 当前登录用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUserId();
    }
}
