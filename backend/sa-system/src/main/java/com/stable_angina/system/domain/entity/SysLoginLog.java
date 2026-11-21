package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志（并发登录允许，但需留痕）
 */
@Data
@TableName("sys_login_log")
@Schema(description = "登录日志")
public class SysLoginLog implements Serializable {

    @Schema(description = "日志主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "客户端 IP")
    private String ip;

    @Schema(description = "浏览器 User-Agent")
    private String userAgent;

    @Schema(description = "登录结果：1 成功 0 失败")
    private Integer status;

    @Schema(description = "提示信息，如：登录成功 / 账号或密码错误")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
