package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录入参
 */
@Data
@Schema(description = "登录入参")
public class LoginDTO {

    @Schema(description = "登录账号", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入账号")
    @Size(max = 64, message = "账号长度不能超过 64")
    private String username;

    @Schema(description = "登录密码", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入密码")
    @Size(max = 64, message = "密码长度不能超过 64")
    private String password;
}
