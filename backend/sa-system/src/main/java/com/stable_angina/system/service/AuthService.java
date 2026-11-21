package com.stable_angina.system.service;

import com.stable_angina.system.domain.dto.LoginDTO;
import com.stable_angina.system.domain.vo.LoginVO;
import com.stable_angina.system.domain.vo.UserInfoVO;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 账号密码登录
     */
    LoginVO login(LoginDTO dto, String ip, String userAgent);

    /**
     * 登出：删除令牌白名单，令牌立即失效
     */
    void logout();

    /**
     * 刷新令牌（轮换 jti）
     */
    LoginVO refresh();

    /**
     * 当前登录用户信息
     */
    UserInfoVO currentUser();
}
