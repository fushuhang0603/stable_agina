package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户分页查询入参
 */
@Data
@Schema(description = "用户分页查询入参")
public class SysUserQueryDTO {

    @Schema(description = "关键字：用户名 / 姓名 / 手机号", example = "张")
    private String keyword;

    @Schema(description = "角色标识：SUPER_ADMIN / DOCTOR / PATIENT", example = "DOCTOR")
    private String roleKey;

    @Schema(description = "状态：1 正常 0 停用", example = "1")
    private Integer status;

    @Schema(description = "当前页码，从 1 开始", example = "1")
    private long pageNum = 1;

    @Schema(description = "每页条数", example = "8")
    private long pageSize = 10;
}
