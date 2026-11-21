package com.stable_angina.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.common.event.DoctorProfileInitEvent;
import com.stable_angina.common.api.BusinessException;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public PageResult<SysUserVO> page(SysUserQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);

        Page<SysUser> userPage = userMapper.selectUserPage(new Page<>(current, size), query);
        List<SysUser> users = userPage.getRecords();

        // 批量补充角色，展示多角色中的第一个
        Map<Long, List<SysRole>> roleMap = new HashMap<>();
        List<Long> userIds = users.stream().map(SysUser::getId).toList();
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<SysUserRole> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.in(SysUserRole::getUserId, userIds);
            List<SysUserRole> relations = userRoleMapper.selectList(relationWrapper);
            List<Long> roleIds = relations.stream().map(SysUserRole::getRoleId).distinct().toList();
            if (!roleIds.isEmpty()) {
                LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.in(SysRole::getId, roleIds);
                Map<Long, SysRole> roleById = roleMapper.selectList(roleWrapper).stream()
                        .collect(Collectors.toMap(SysRole::getId, role -> role));
                relations.forEach(relation -> {
                    SysRole role = roleById.get(relation.getRoleId());
                    if (role != null) {
                        roleMap.computeIfAbsent(relation.getUserId(), key -> new ArrayList<>()).add(role);
                    }
                });
            }
        }

        List<SysUserVO> rows = users.stream().map(user -> {
            SysUserVO vo = BeanUtil.copyProperties(user, SysUserVO.class);
            String org = StringUtils.hasText(user.getHospital()) ? user.getHospital() : "";
            if (StringUtils.hasText(user.getDepartment())) {
                org = StringUtils.hasText(org) ? org + " / " + user.getDepartment() : user.getDepartment();
            }
            vo.setOrg(org);
            List<SysRole> roles = roleMap.getOrDefault(user.getId(), List.of());
            vo.setRoleKey(roles.isEmpty() ? "" : roles.get(0).getRoleKey());
            vo.setRoleName(roles.isEmpty() ? "" : roles.get(0).getRoleName());
            vo.setStatusText(user.getStatus() != null && user.getStatus() == CommonConstants.STATUS_ENABLED ? "启用" : "停用");
            return vo;
        }).toList();

        PageResult<SysUserVO> result = new PageResult<>();
        result.setRecords(rows);
        result.setTotal(userPage.getTotal());
        result.setPageNum(current);
        result.setPageSize(size);
        return result;
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
            throw new BusinessException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, dto.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException(ErrorCodes.PHONE_EXISTS, "手机号已存在");
            }
        }
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, dto.getRoleKey());
        roleWrapper.eq(SysRole::getStatus, CommonConstants.STATUS_ENABLED);
        SysRole role = roleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "角色不存在或已停用");
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

        // 医生账号同步初始化医生档案
        if ("DOCTOR".equals(role.getRoleKey())) {
            eventPublisher.publishEvent(new DoctorProfileInitEvent(user.getId(), operatorId));
        }

        log.info("新增账号成功: id={}, username={}, roleKey={}", user.getId(), username, role.getRoleKey());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SysUserUpdateDTO dto) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, dto.getPhone());
            phoneWrapper.ne(SysUser::getId, id);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException(ErrorCodes.PHONE_EXISTS, "手机号已存在");
            }
        }
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, dto.getRoleKey());
        roleWrapper.eq(SysRole::getStatus, CommonConstants.STATUS_ENABLED);
        SysRole role = roleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "角色不存在或已停用");
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
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "状态值不合法");
        }
        if (userMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
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
            throw new BusinessException(ErrorCodes.USER_NOT_FOUND, "用户不存在");
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
