package com.stable_angina.system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.Result;
import com.stable_angina.common.constant.AuthConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 解析 Bearer 令牌 → 校验 Redis 白名单 → 写入 SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(AuthConstants.AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(AuthConstants.TOKEN_PREFIX)) {
            String token = header.substring(AuthConstants.TOKEN_PREFIX.length());
            Claims claims = jwtTokenProvider.parse(token);
            if (claims != null) {
                Long userId = jwtTokenProvider.getUserId(claims);
                String jti = claims.getId();
                LoginUser loginUser = tokenStore.load(userId, jti);
                if (loginUser == null) {
                    // 已登出或已过期：令牌立即失效
                    writeUnauthorized(response, "登录状态已失效，请重新登录");
                    return;
                }
                tokenStore.renew(userId, jti);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.fail(ErrorCodes.UNAUTHORIZED, message)));
    }
}
