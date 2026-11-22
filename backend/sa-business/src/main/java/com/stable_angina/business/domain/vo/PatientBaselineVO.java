package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 患者基线档案
 */
@Data
@Schema(description = "患者基线档案")
public class PatientBaselineVO implements Serializable {

    @Schema(description = "基线条目主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "关联 patient.id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long patientId;

    @Schema(description = "首次发作描述")
    private String firstAttack;

    @Schema(description = "检查结果")
    private String examResult;

    @Schema(description = "危险分层依据")
    private String riskBasis;

    @Schema(description = "建档医生（sys_user.id）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long registerDoctorId;

    @Schema(description = "建档医生姓名")
    private String registerDoctorName;

    @Schema(description = "建档日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate registerDate;
}
