package com.stable_angina.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.common.exception.BizException;
import com.stable_angina.system.domain.dto.SysUserCreateDTO;
import com.stable_angina.system.domain.dto.SysUserQueryDTO;
import com.stable_angina.system.domain.dto.SysUserUpdateDTO;
import com.stable_angina.system.domain.entity.SysRole;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.domain.entity.SysUserRole;
import com.stable_angina.system.domain.vo.SysUserStatsVO;
import com.stable_angina.system.domain.vo.SysUserVO;
import com.stable_angina.system.mapper.SysRoleMapper;
import com.stable_angina.system.mapper.SysUserMapper;
import com.stable_angina.system.mapper.SysUserRoleMapper;
import com.stable_angina.system.security.SecurityUtils;
import com.stable_angina.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUserVO> page(SysUserQueryDTO query) {
        long pageNum = Math.max(query.getPageNum(), 1);
        long pageSize = Math.min(Math.max(query.getPageSize(), 1), 100);
        long offset = (pageNum - 1) * pageSize;

        long total = userMapper.countUser(query);
        List<SysUserVO> rows = userMapper.selectUserPage(query, offset, pageSize);
        // 展示字段：机构拼接、多角色取第一个、状态文本
        rows.forEach(vo -> {
            String org = StringUtils.hasText(vo.getHospital()) ? vo.getHospital() : "";
            if (StringUtils.hasText(vo.getDepartment())) {
                org = StringUtils.hasText(org) ? org + " / " + vo.getDepartment() : vo.getDepartment();
            }
            vo.setOrg(org);
            vo.setRoleKey(StringUtils.hasText(vo.getRoleKey()) ? vo.getRoleKey().split(",")[0] : "");
            vo.setRoleName(StringUtils.hasText(vo.getRoleName()) ? vo.getRoleName().split(",")[0] : "");
            vo.setStatusText(vo.getStatus() != null && vo.getStatus() == CommonConstants.STATUS_ENABLED ? "启用" : "停用");
        });
        return PageResult.of(rows, total, pageNum, pageSize);
    }

    @Override
    public SysUserStatsVO stats() {
        return userMapper.selectStats();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysUserCreateDTO dto) {
        String username = dto.getUsername().trim();
        LambdaQueryWrapper<SysUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUser::getUsername, username);
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BizException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, dto.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BizException(ErrorCodes.PHONE_EXISTS, "手机号已存在");
            }
        }
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, dto.getRoleKey());
        roleWrapper.eq(SysRole::getStatus, CommonConstants.STATUS_ENABLED);
        SysRole role = roleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "角色不存在或已停用");
        }

        SysUser user = BeanUtil.copyProperties(dto, SysUser.class);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName().trim());
        if (user.getStatus() == null) {
            user.setStatus(CommonConstants.STATUS_ENABLED);
        }
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = SecurityUtils.getUserId();
        user.setCreateBy(operatorId);
        user.setCreateTime(now);
        user.setUpdateBy(operatorId);
        user.setUpdateTime(now);
        userMapper.insert(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);

        log.info("新增账号成功: id={}, username={}, roleKey={}", user.getId(), username, role.getRoleKey());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SysUserUpdateDTO dto) {
        if (userMapper.selectById(id) == null) {
            throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, dto.getPhone());
            phoneWrapper.ne(SysUser::getId, id);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BizException(ErrorCodes.PHONE_EXISTS, "手机号已存在");
            }
        }
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, dto.getRoleKey());
        roleWrapper.eq(SysRole::getStatus, CommonConstants.STATUS_ENABLED);
        SysRole role = roleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "角色不存在或已停用");
        }

        SysUser update = BeanUtil.copyProperties(dto, SysUser.class);
        update.setId(id);
        update.setRealName(dto.getRealName().trim());
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);

        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, id);
        userRoleMapper.delete(userRoleWrapper);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(id);
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);
        log.info("编辑账号成功: id={}, roleKey={}", id, role.getRoleKey());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != CommonConstants.STATUS_ENABLED && status != CommonConstants.STATUS_DISABLED)) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "状态值不合法");
        }
        if (userMapper.selectById(id) == null) {
            throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(status);
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);
        log.info("账号状态变更: id={}, status={}", id, status);
    }

    @Override
    public void resetPassword(Long id, String password) {
        if (userMapper.selectById(id) == null) {
            throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setPassword(passwordEncoder.encode(password));
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);
        log.info("重置账号密码: id={}", id);
    }
}
