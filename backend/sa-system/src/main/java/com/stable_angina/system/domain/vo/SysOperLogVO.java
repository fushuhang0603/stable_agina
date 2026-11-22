package com.stable_angina.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志行数据
 */
@Data
@Schema(description = "操作日志行数据")
public class SysOperLogVO implements Serializable {

    @Schema(description = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
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

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "异常信息")
    private String errorMsg;

    @Schema(description = "耗时（毫秒）")
    private Long costTime;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime operTime;
}
