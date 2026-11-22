package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 随访参数行数据
 */
@Data
@Schema(description = "随访参数")
public class FollowupParamVO implements Serializable {

    @Schema(description = "参数主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "参数键，全局唯一")
    private String paramKey;

    @Schema(description = "参数名称")
    private String paramName;

    @Schema(description = "参数分组")
    private String paramGroup;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "值类型：NUMBER / TEXT / BOOLEAN")
    private String valueType;

    @Schema(description = "参数说明")
    private String description;

    @Schema(description = "分组内排序")
    private Integer sort;

    @Schema(description = "状态：1 启用 0 停用")
    private Integer status;

    @Schema(description = "状态文本：启用 / 停用")
    private String statusText;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
