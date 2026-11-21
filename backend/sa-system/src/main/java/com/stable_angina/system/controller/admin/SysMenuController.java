package com.stable_angina.system.controller.admin;

import com.stable_angina.common.api.Result;
import com.stable_angina.system.domain.vo.MenuTreeVO;
import com.stable_angina.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单权限接口，完整路径 = /stableanginaapi/api/system/menus/**
 */
@Tag(name = "菜单权限")
@RestController
@RequestMapping("/api/system/menus")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @Operation(summary = "菜单权限树")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        return Result.ok(sysMenuService.tree());
    }
}
