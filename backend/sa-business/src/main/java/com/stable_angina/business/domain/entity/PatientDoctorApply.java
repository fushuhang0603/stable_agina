package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者绑定医生申请
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient_doctor_apply")
@Schema(description = "患者绑定医生申请")
public class PatientDoctorApply extends BaseEntity {

    @Schema(description = "申请主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联 patient.id")
    private Long patientId;

    @Schema(description = "申请人 sys_user.id")
    private Long userId;

    @Schema(description = "目标医生 sys_user.id")
    private Long doctorId;

    @Schema(description = "申请类型：1 首次绑定 2 换绑")
    private Integer applyType;

    @Schema(description = "状态：1 待审核 2 已通过 3 已驳回 4 已取消")
    private Integer status;

    @Schema(description = "申请说明")
    private String applyReason;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "处理人 sys_user.id")
    private Long handleBy;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
}
