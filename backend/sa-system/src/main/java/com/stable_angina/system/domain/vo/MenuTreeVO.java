package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限树节点
 */
@Data
@Schema(description = "菜单权限树节点")
public class MenuTreeVO implements Serializable {

    @Schema(description = "菜单主键")
    private Long id;

    @Schema(description = "父级菜单 ID，顶级为 0")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "权限标识，如 menu:user")
    private String key;

    @Schema(description = "子节点")
    private List<MenuTreeVO> children = new ArrayList<>();
}
