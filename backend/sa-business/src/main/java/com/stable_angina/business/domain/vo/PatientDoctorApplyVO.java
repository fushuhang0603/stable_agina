package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者绑定医生申请行数据
 */
@Data
@Schema(description = "患者绑定医生申请")
public class PatientDoctorApplyVO implements Serializable {

    @Schema(description = "申请主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "患者主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long patientId;

    @Schema(description = "患者编号")
    private String patientNo;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者手机号")
    private String patientPhone;

    @Schema(description = "危险分层：1 低危 2 中危 3 高危")
    private Integer riskLevel;

    @Schema(description = "危险分层文本")
    private String riskLevelText;

    @Schema(description = "申请人 sys_user.id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @Schema(description = "目标医生 sys_user.id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "医生职称")
    private String doctorTitle;

    @Schema(description = "医生所属医院")
    private String doctorHospital;

    @Schema(description = "医生所属科室")
    private String doctorDepartment;

    @Schema(description = "申请类型：1 首次绑定 2 换绑")
    private Integer applyType;

    @Schema(description = "申请类型文本")
    private String applyTypeText;

    @Schema(description = "状态：1 待审核 2 已通过 3 已驳回 4 已取消")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "申请说明")
    private String applyReason;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "处理人 sys_user.id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long handleBy;

    @Schema(description = "处理人姓名")
    private String handleName;

    @Schema(description = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime handleTime;

    @Schema(description = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
