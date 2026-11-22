package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 患者基线档案
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient_baseline")
@Schema(description = "患者基线档案")
public class PatientBaseline extends BaseEntity {

    @Schema(description = "基线条目主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联 patient.id")
    private Long patientId;

    @Schema(description = "首次发作描述")
    private String firstAttack;

    @Schema(description = "检查结果")
    private String examResult;

    @Schema(description = "危险分层依据")
    private String riskBasis;

    @Schema(description = "建档医生（sys_user.id）")
    private Long registerDoctorId;

    @Schema(description = "建档日期")
    private LocalDate registerDate;
}
