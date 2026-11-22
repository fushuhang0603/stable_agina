package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者档案变更留痕
 */
@Data
@TableName("patient_change_log")
@Schema(description = "患者档案变更留痕")
public class PatientChangeLog implements Serializable {

    @Schema(description = "留痕主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联 patient.id")
    private Long patientId;

    @Schema(description = "变更字段名称")
    private String fieldName;

    @Schema(description = "变更前值")
    private String beforeValue;

    @Schema(description = "变更后值")
    private String afterValue;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "变更时间")
    private LocalDateTime changeTime;
}
