package com.stable_angina.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.system.domain.dto.SysRoleGrantDTO;
import com.stable_angina.system.domain.entity.SysRole;
import com.stable_angina.system.domain.entity.SysRoleMenu;
import com.stable_angina.system.domain.vo.SysRoleVO;
import com.stable_angina.system.mapper.SysRoleMapper;
import com.stable_angina.system.mapper.SysRoleMenuMapper;
import com.stable_angina.system.security.SecurityUtils;
import com.stable_angina.system.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysRoleVO> list() {
        return roleMapper.selectRoleListWithUserCount();
    }

    @Override
    public List<Long> menuIds(Long roleId) {
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "角色不存在");
        }
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grant(Long roleId, SysRoleGrantDTO dto) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "角色不存在");
        }

        SysRole update = BeanUtil.copyProperties(dto, SysRole.class);
        update.setId(roleId);
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(update);

        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(roleMenuWrapper);
        if (dto.getMenuIds() != null) {
            dto.getMenuIds().stream().distinct().forEach(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            });
        }
        log.info("角色授权成功: roleKey={}, dataScope={}, menuCount={}",
                role.getRoleKey(), dto.getDataScope(), dto.getMenuIds() == null ? 0 : dto.getMenuIds().size());
    }
}
