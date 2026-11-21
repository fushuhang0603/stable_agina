package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码入参
 */
@Data
@Schema(description = "重置密码入参")
public class SysUserPasswordDTO {

    @Schema(description = "新密码", example = "Sa@123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入新密码")
    private String password;
}
