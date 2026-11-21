package com.stable_angina.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.constant.AuthConstants;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.system.domain.dto.LoginDTO;
import com.stable_angina.system.domain.entity.SysLoginLog;
import com.stable_angina.system.domain.entity.SysRole;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.mapper.SysLoginLogMapper;
import com.stable_angina.system.mapper.SysMenuMapper;
import com.stable_angina.system.mapper.SysRoleMapper;
import com.stable_angina.system.mapper.SysUserMapper;
import com.stable_angina.system.security.JwtTokenProvider;
import com.stable_angina.system.security.LoginUser;
import com.stable_angina.system.security.SecurityUtils;
import com.stable_angina.system.security.TokenStore;
import com.stable_angina.system.service.AuthService;
import com.stable_angina.system.domain.vo.LoginVO;
import com.stable_angina.system.domain.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysMenuMapper menuMapper;
    @Autowired
    private SysLoginLogMapper loginLogMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private TokenStore tokenStore;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginDTO dto, String ip, String userAgent) {
        String username = dto.getUsername().trim();

        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(userWrapper);
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            // 不区分"账号不存在"与"密码错误"，防止账号枚举
            SysLoginLog loginLogEntity = new SysLoginLog();
            loginLogEntity.setUsername(username);
            loginLogEntity.setIp(ip);
            loginLogEntity.setUserAgent(userAgent);
            loginLogEntity.setStatus(0);
            loginLogEntity.setMsg("账号或密码错误");
            loginLogEntity.setLoginTime(LocalDateTime.now());
            loginLogMapper.insert(loginLogEntity);
            throw new BusinessException(ErrorCodes.BAD_CREDENTIALS, "账号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != CommonConstants.STATUS_ENABLED) {
            SysLoginLog loginLogEntity = new SysLoginLog();
            loginLogEntity.setUsername(username);
            loginLogEntity.setIp(ip);
            loginLogEntity.setUserAgent(userAgent);
            loginLogEntity.setStatus(0);
            loginLogEntity.setMsg("账号已停用");
            loginLogEntity.setLoginTime(LocalDateTime.now());
            loginLogMapper.insert(loginLogEntity);
            throw new BusinessException(ErrorCodes.USER_DISABLED, "账号已停用，请联系管理员");
        }

        LoginUser loginUser = buildLoginUser(user);
        String token = tokenProvider.createToken(loginUser);
        tokenStore.save(loginUser);

        LambdaUpdateWrapper<SysUser> loginWrapper = new LambdaUpdateWrapper<>();
        loginWrapper.set(SysUser::getLastLoginTime, LocalDateTime.now());
        loginWrapper.set(SysUser::getLastLoginIp, ip);
        loginWrapper.eq(SysUser::getId, user.getId());
        userMapper.update(null, loginWrapper);

        SysLoginLog loginLogEntity = new SysLoginLog();
        loginLogEntity.setUsername(username);
        loginLogEntity.setIp(ip);
        loginLogEntity.setUserAgent(userAgent);
        loginLogEntity.setStatus(1);
        loginLogEntity.setMsg("登录成功");
        loginLogEntity.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(loginLogEntity);

        return new LoginVO(token, tokenProvider.getTtl().toSeconds(), loginUser.toUserInfoVO());
    }

    @Override
    public void logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            tokenStore.remove(loginUser.getUserId(), loginUser.getJti());
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    public LoginVO refresh() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
        }
        // 令牌轮换：旧 jti 作废，签发新令牌
        tokenStore.remove(loginUser.getUserId(), loginUser.getJti());
        String token = tokenProvider.createToken(loginUser);
        tokenStore.save(loginUser);
        return new LoginVO(token, tokenProvider.getTtl().toSeconds(), loginUser.toUserInfoVO());
    }

    @Override
    public UserInfoVO currentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
        }
        return loginUser.toUserInfoVO();
    }

    private LoginUser buildLoginUser(SysUser user) {
        List<SysRole> roles = roleMapper.selectEnabledByUserId(user.getId());
        List<String> roleKeys = roles.stream().map(SysRole::getRoleKey).toList();
        // 前端 org 字段：医院 / 科室
        String org = StringUtils.hasText(user.getHospital()) ? user.getHospital() : "";
        if (StringUtils.hasText(user.getDepartment())) {
            org = StringUtils.hasText(org) ? org + " / " + user.getDepartment() : user.getDepartment();
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setOrg(org);
        loginUser.setRoles(roleKeys);
        loginUser.setPermissions(roleKeys.contains(AuthConstants.ROLE_SUPER_ADMIN)
                ? List.of(AuthConstants.PERMISSION_ALL)
                : menuMapper.selectPermissionsByUserId(user.getId()));
        return loginUser;
    }
}
