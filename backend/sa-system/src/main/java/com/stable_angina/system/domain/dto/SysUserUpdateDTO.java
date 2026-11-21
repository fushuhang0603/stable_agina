package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑用户入参（登录账号不可修改）
 */
@Data
@Schema(description = "编辑用户入参")
public class SysUserUpdateDTO {

    @Schema(description = "姓名", example = "王医生", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入姓名")
    @Size(max = 64, message = "姓名长度不能超过 64")
    private String realName;

    @Schema(description = "手机号", example = "13900000001")
    @Size(max = 20, message = "手机号长度不能超过 20")
    private String phone;

    @Schema(description = "邮箱", example = "doctor02@example.com")
    @Size(max = 128, message = "邮箱长度不能超过 128")
    private String email;

    @Schema(description = "职称", example = "主治医师")
    @Size(max = 64, message = "职称长度不能超过 64")
    private String title;

    @Schema(description = "所属医院", example = "附属医院")
    @Size(max = 128, message = "医院长度不能超过 128")
    private String hospital;

    @Schema(description = "所属科室", example = "心内科")
    @Size(max = 128, message = "科室长度不能超过 128")
    private String department;

    @Schema(description = "角色标识", example = "DOCTOR", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择角色")
    private String roleKey;

    @Schema(description = "状态：1 正常 0 停用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}
