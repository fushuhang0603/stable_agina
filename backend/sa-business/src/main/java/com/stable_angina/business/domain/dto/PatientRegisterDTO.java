package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 患者注册入参
 */
@Data
@Schema(description = "患者注册")
public class PatientRegisterDTO implements Serializable {

    @Schema(description = "登录用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "登录密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String realName;

    @Schema(description = "身份证号，用于实名校验")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
