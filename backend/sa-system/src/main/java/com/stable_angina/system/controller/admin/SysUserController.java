package com.stable_angina.system.controller.admin;

import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.api.Result;
import com.stable_angina.system.annotation.OperLog;
import com.stable_angina.system.domain.dto.SysUserCreateDTO;
import com.stable_angina.system.domain.dto.SysUserPasswordDTO;
import com.stable_angina.system.domain.dto.SysUserQueryDTO;
import com.stable_angina.system.domain.dto.SysUserStatusDTO;
import com.stable_angina.system.domain.dto.SysUserUpdateDTO;
import com.stable_angina.system.domain.vo.SysUserStatsVO;
import com.stable_angina.system.domain.vo.SysUserVO;
import com.stable_angina.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账号管理接口，完整路径 = /stableanginaapi/api/system/users/**
 */
@Tag(name = "账号管理")
@RestController
@RequestMapping("/api/system/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "分页查询账号列表")
    @GetMapping
    public Result<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
        return Result.success(sysUserService.page(query));
    }

    @Operation(summary = "账号统计概览")
    @GetMapping("/stats")
    public Result<SysUserStatsVO> stats() {
        return Result.success(sysUserService.stats());
    }

    @Operation(summary = "新增账号")
    @OperLog(module = "账号管理", action = "新增")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SysUserCreateDTO dto) {
        return Result.success(sysUserService.create(dto));
    }

    @Operation(summary = "编辑账号")
    @OperLog(module = "账号管理", action = "修改")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysUserUpdateDTO dto) {
        sysUserService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "启用 / 停用账号")
    @OperLog(module = "账号管理", action = "启用/停用")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody SysUserStatusDTO dto) {
        sysUserService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @OperLog(module = "账号管理", action = "重置密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody SysUserPasswordDTO dto) {
        sysUserService.resetPassword(id, dto.getPassword());
        return Result.success();
    }
}
