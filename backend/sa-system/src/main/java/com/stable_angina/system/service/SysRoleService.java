package com.stable_angina.system.service;

import com.stable_angina.system.domain.dto.SysRoleGrantDTO;
import com.stable_angina.system.domain.vo.SysRoleVO;

import java.util.List;

/**
 * 角色与权限服务
 */
public interface SysRoleService {

    /**
     * 角色列表（含绑定用户数、已授权菜单数）
     */
    List<SysRoleVO> list();

    /**
     * 角色已授权的菜单 ID
     */
    List<Long> menuIds(Long roleId);

    /**
     * 保存角色权限：更新数据范围 + 重写角色菜单关联
     */
    void grant(Long roleId, SysRoleGrantDTO dto);
}
