package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户与角色关联（联合主键 user_id + role_id，无审计字段）
 */
@Data
@TableName("sys_user_role")
@Schema(description = "用户与角色关联")
public class SysUserRole implements Serializable {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "角色 ID")
    private Long roleId;
}
