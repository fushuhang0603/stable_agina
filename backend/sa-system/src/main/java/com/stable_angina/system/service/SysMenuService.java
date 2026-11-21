package com.stable_angina.system.service;

import com.stable_angina.system.domain.vo.MenuTreeVO;

import java.util.List;

/**
 * 菜单权限服务
 */
public interface SysMenuService {

    /**
     * 已启用菜单的权限树
     */
    List<MenuTreeVO> tree();
}
