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
 * 患者医生绑定留痕
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient_doctor_bind_log")
@Schema(description = "患者医生绑定留痕")
public class PatientDoctorBindLog extends BaseEntity {

    @Schema(description = "留痕主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联 patient.id")
    private Long patientId;

    @Schema(description = "医生 sys_user.id")
    private Long doctorId;

    @Schema(description = "动作：BIND 绑定 / UNBIND 解绑 / CHANGE 换绑")
    private String action;

    @Schema(description = "操作人类型：PATIENT 患者 / DOCTOR 医生 / ADMIN 管理员")
    private String operatorType;

    @Schema(description = "操作人 sys_user.id")
    private Long operatorId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;
}
