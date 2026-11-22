package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体，对应 sys_oper_log
 */
@Data
@TableName("sys_oper_log")
@Schema(description = "操作日志")
public class SysOperLog {

    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "模块名称")
    private String title;

    @Schema(description = "动作名称")
    private String businessType;

    @Schema(description = "方法全限定名")
    private String method;

    @Schema(description = "HTTP 方法")
    private String requestMethod;

    @Schema(description = "操作人账号")
    private String operName;

    @Schema(description = "请求地址")
    private String operUrl;

    @Schema(description = "操作 IP")
    private String operIp;

    @Schema(description = "请求入参")
    private String operParam;

    @Schema(description = "返回结果")
    private String jsonResult;

    @Schema(description = "状态：1 成功 0 失败")
    private Integer status;

    @Schema(description = "异常信息")
    private String errorMsg;

    @Schema(description = "耗时（毫秒）")
    private Long costTime;

    @Schema(description = "操作时间")
    private LocalDateTime operTime;
}
