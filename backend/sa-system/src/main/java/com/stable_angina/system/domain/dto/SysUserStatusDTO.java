package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户启停用入参
 */
@Data
@Schema(description = "用户启停用入参")
public class SysUserStatusDTO {

    @Schema(description = "状态：1 正常 0 停用", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请传入状态")
    private Integer status;
}
