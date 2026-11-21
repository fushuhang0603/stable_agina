package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色与菜单关联
 */
@Data
@TableName("sys_role_menu")
@Schema(description = "角色与菜单关联")
public class SysRoleMenu implements Serializable {

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "菜单 ID")
    private Long menuId;
}
