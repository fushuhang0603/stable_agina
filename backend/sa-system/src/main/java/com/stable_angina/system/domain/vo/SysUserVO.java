package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号列表行数据
 */
@Data
@Schema(description = "账号列表行数据")
public class SysUserVO implements Serializable {

    @Schema(description = "用户主键")
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "所属医院")
    private String hospital;

    @Schema(description = "所属科室")
    private String department;

    @Schema(description = "所属机构，格式：医院 / 科室")
    private String org;

    @Schema(description = "角色标识，多角色时取第一个")
    private String roleKey;

    @Schema(description = "角色名称，多角色时取第一个")
    private String roleName;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "状态文本：启用 / 停用")
    private String statusText;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "备注")
    private String remark;
}
