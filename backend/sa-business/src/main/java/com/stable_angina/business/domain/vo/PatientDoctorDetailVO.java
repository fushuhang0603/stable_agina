package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 患者端医生档案详情
 */
@Data
@Schema(description = "患者端医生档案详情")
public class PatientDoctorDetailVO implements Serializable {

    @Schema(description = "医生账号主键（sys_user.id）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "医生姓名")
    private String name;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "行政职务")
    private String position;

    @Schema(description = "所属医院")
    private String hospital;

    @Schema(description = "所属科室")
    private String department;

    @Schema(description = "执业类别")
    private String licenseType;

    @Schema(description = "执业范围")
    private String practiceScope;

    @Schema(description = "擅长领域")
    private String specialty;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "工作与教育经历")
    private String experience;

    @Schema(description = "出诊时间")
    private String schedule;

    @Schema(description = "挂号费（元）")
    private BigDecimal registrationFee;

    @Schema(description = "在管患者上限")
    private Integer maxPatients;

    @Schema(description = "当前在管患者数")
    private Integer patientCount;

    @Schema(description = "是否当前绑定医生")
    private Boolean bound;

    @Schema(description = "是否已提交待审核申请")
    private Boolean pending;
}
