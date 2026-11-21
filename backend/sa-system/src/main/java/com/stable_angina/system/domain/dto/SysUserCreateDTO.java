package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增用户入参
 */
@Data
@Schema(description = "新增用户入参")
public class SysUserCreateDTO {

    @Schema(description = "登录账号", example = "doctor02", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入登录账号")
    private String username;

    @Schema(description = "初始密码", example = "Sa@123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入初始密码")
    private String password;

    @Schema(description = "姓名", example = "王医生", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入姓名")
    private String realName;

    @Schema(description = "手机号", example = "13900000001")
    private String phone;

    @Schema(description = "邮箱", example = "doctor02@example.com")
    private String email;

    @Schema(description = "职称", example = "主治医师")
    private String title;

    @Schema(description = "所属医院", example = "附属医院")
    private String hospital;

    @Schema(description = "所属科室", example = "心内科")
    private String department;

    @Schema(description = "角色标识", example = "DOCTOR", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择角色")
    private String roleKey;

    @Schema(description = "状态：1 正常 0 停用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
