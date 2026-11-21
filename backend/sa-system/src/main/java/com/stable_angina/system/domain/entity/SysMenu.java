package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单与权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单与权限")
public class SysMenu extends BaseEntity {

    @Schema(description = "菜单主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "父级菜单 ID，顶级为 0")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型：M 目录 / C 菜单 / F 按钮")
    private String menuType;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "权限标识，如 menu:dashboard")
    private String permission;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序号，升序")
    private Integer sort;

    @Schema(description = "是否显示：1 显示 0 隐藏")
    private Integer visible;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;
}
