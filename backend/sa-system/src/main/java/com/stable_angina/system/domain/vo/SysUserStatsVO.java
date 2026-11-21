package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账号统计概览
 */
@Data
@Schema(description = "账号统计概览")
public class SysUserStatsVO implements Serializable {

    @Schema(description = "账号总数")
    private Long total;

    @Schema(description = "启用账号数")
    private Long enabled;

    @Schema(description = "停用账号数")
    private Long disabled;

    @Schema(description = "医生角色账号数")
    private Long doctor;
}
