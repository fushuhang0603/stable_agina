package com.stable_angina.system.controller.admin;

import com.stable_angina.common.api.Result;
import com.stable_angina.system.annotation.OperLog;
import com.stable_angina.system.domain.dto.SysRoleGrantDTO;
import com.stable_angina.system.domain.vo.SysRoleVO;
import com.stable_angina.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色与权限接口，完整路径 = /stableanginaapi/api/system/roles/**
 */
@Tag(name = "角色与权限")
@RestController
@RequestMapping("/api/system/roles")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Operation(summary = "角色列表")
    @GetMapping
    public Result<List<SysRoleVO>> list() {
        return Result.success(sysRoleService.list());
    }

    @Operation(summary = "角色已授权的菜单 ID")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> menuIds(@PathVariable Long id) {
        return Result.success(sysRoleService.menuIds(id));
    }

    @Operation(summary = "保存角色权限分配")
    @OperLog(module = "角色与权限", action = "授权")
    @PutMapping("/{id}/grant")
    public Result<Void> grant(@PathVariable Long id, @Valid @RequestBody SysRoleGrantDTO dto) {
        sysRoleService.grant(id, dto);
        return Result.success();
    }
}
