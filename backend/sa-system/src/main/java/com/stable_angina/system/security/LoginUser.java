package com.stable_angina.system.security;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stable_angina.system.domain.vo.UserInfoVO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 认证主体：序列化后存入 Redis 白名单 auth:token:{userId}:{jti}
 */
@Data
public class LoginUser implements UserDetails, Serializable {

    private Long userId;
    private String username;
    private String realName;
    private String org;
    private List<String> roles = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    /** 当前令牌唯一标识，登出时精确删除 Redis key */
    private String jti;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        if (permissions != null) {
            permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 转为前端返回结构
     */
    public UserInfoVO toUserInfoVO() {
        return BeanUtil.copyProperties(this, UserInfoVO.class);
    }
}
