package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息（字段名与前端 stores/auth.js 的 user 结构一致）
 */
@Data
@Schema(description = "当前登录用户信息")
public class UserInfoVO implements Serializable {

    @Schema(description = "用户主键")
    private Long userId;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "所属机构，格式：医院 / 科室")
    private String org;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "角色标识列表：SUPER_ADMIN / DOCTOR / PATIENT")
    private List<String> roles;

    @Schema(description = "菜单权限标识列表，超级管理员为 [\"*\"]")
    private List<String> permissions;
}
