package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色")
public class SysRole extends BaseEntity {

    @Schema(description = "角色主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识：SUPER_ADMIN / DOCTOR / PATIENT")
    private String roleKey;

    @Schema(description = "数据范围：ALL 全部 / ASSIGNED_PATIENTS 负责患者 / SELF 仅本人")
    private String dataScope;

    @Schema(description = "排序号，升序")
    private Integer sort;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
