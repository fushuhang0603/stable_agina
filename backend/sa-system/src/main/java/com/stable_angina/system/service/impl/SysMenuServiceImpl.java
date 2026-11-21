package com.stable_angina.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.domain.entity.SysMenu;
import com.stable_angina.system.domain.vo.MenuTreeVO;
import com.stable_angina.system.mapper.SysMenuMapper;
import com.stable_angina.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<MenuTreeVO> tree() {
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(SysMenu::getStatus, CommonConstants.STATUS_ENABLED);
        menuWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(menuWrapper);

        // 按排序结果建立节点索引，保证同级顺序与数据库 sort 一致
        Map<Long, MenuTreeVO> nodeMap = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            nodeMap.put(menu.getId(), toNode(menu));
        }

        List<MenuTreeVO> roots = new ArrayList<>();
        for (SysMenu menu : menus) {
            MenuTreeVO node = nodeMap.get(menu.getId());
            Long parentId = menu.getParentId();
            MenuTreeVO parent = parentId == null ? null : nodeMap.get(parentId);
            if (parentId == null || parentId == CommonConstants.ROOT_PARENT_ID || parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    private MenuTreeVO toNode(SysMenu menu) {
        MenuTreeVO node = new MenuTreeVO();
        node.setId(menu.getId());
        node.setParentId(menu.getParentId());
        node.setTitle(menu.getMenuName());
        node.setKey(menu.getPermission());
        return node;
    }
}
