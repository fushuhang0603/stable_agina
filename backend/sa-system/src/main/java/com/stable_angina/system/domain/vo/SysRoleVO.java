package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色列表行数据
 */
@Data
@Schema(description = "角色列表行数据")
public class SysRoleVO implements Serializable {

    @Schema(description = "角色主键")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识：SUPER_ADMIN / DOCTOR / PATIENT")
    private String roleKey;

    @Schema(description = "数据范围：ALL / ASSIGNED_PATIENTS / SELF")
    private String dataScope;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "绑定用户数")
    private Long userCount;

    @Schema(description = "已授权菜单数")
    private Long menuCount;
}
