package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作日志分页查询入参
 */
@Data
@Schema(description = "操作日志分页查询入参")
public class SysOperLogQueryDTO {

    @Schema(description = "操作人账号（模糊匹配）", example = "admin")
    private String operName;

    @Schema(description = "请求地址（模糊匹配）", example = "/api/system/users")
    private String operUrl;

    @Schema(description = "模块名称", example = "账号管理")
    private String title;

    @Schema(description = "动作名称", example = "新增")
    private String businessType;

    @Schema(description = "状态：1 成功 0 失败", example = "1")
    private Integer status;

    @Schema(description = "当前页码，从 1 开始", example = "1")
    private long pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private long pageSize = 10;
}
