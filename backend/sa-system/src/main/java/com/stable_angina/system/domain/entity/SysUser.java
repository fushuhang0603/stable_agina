package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser extends BaseEntity {

    @Schema(description = "用户主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "登录密码（BCrypt 密文）", hidden = true)
    private String password;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别：0 未知 1 男 2 女")
    private Integer gender;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "所属医院")
    private String hospital;

    @Schema(description = "所属科室")
    private String department;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录 IP")
    private String lastLoginIp;

    @Schema(description = "备注")
    private String remark;
}
