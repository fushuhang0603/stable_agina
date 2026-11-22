package com.stable_angina.system.controller.admin;

import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.api.Result;
import com.stable_angina.system.domain.dto.SysOperLogQueryDTO;
import com.stable_angina.system.domain.vo.SysOperLogStatsVO;
import com.stable_angina.system.domain.vo.SysOperLogVO;
import com.stable_angina.system.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志接口，完整路径 = /stableanginaapi/api/system/oper-logs/**
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/system/oper-logs")
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<PageResult<SysOperLogVO>> page(SysOperLogQueryDTO query) {
        return Result.success(sysOperLogService.page(query));
    }

    @Operation(summary = "操作日志统计概览")
    @GetMapping("/stats")
    public Result<SysOperLogStatsVO> stats() {
        return Result.success(sysOperLogService.stats());
    }
}
