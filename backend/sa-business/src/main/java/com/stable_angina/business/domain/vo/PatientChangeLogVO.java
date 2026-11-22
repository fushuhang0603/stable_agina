package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者档案变更留痕
 */
@Data
@Schema(description = "患者档案变更留痕")
public class PatientChangeLogVO implements Serializable {

    @Schema(description = "留痕主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "变更字段名称")
    private String fieldName;

    @Schema(description = "变更前值")
    private String beforeValue;

    @Schema(description = "变更后值")
    private String afterValue;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "变更时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime changeTime;
}
