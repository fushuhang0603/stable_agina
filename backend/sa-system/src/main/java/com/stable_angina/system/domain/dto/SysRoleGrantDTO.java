package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色权限分配入参
 */
@Data
@Schema(description = "角色权限分配入参")
public class SysRoleGrantDTO {

    @Schema(description = "数据范围：ALL 全部 / ASSIGNED_PATIENTS 本人负责患者 / SELF 仅本人",
            example = "ASSIGNED_PATIENTS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择数据范围")
    private String dataScope;

    @Schema(description = "已勾选的菜单 ID 列表")
    private List<Long> menuIds;
}
