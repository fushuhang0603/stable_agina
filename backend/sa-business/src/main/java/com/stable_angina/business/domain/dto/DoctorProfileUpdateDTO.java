package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 编辑医生档案入参：同步更新账号基础信息与档案信息
 */
@Data
@Schema(description = "编辑医生档案入参")
public class DoctorProfileUpdateDTO {

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "职称", example = "主任医师")
    private String title;

    @Schema(description = "所属医院")
    private String hospital;

    @Schema(description = "所属科室")
    private String department;

    @Schema(description = "执业证书编号")
    private String licenseNo;

    @Schema(description = "执业类别：临床 / 中医 / 口腔 / 公共卫生")
    private String licenseType;

    @Schema(description = "执业范围")
    private String practiceScope;

    @Schema(description = "资格证书编号")
    private String certNo;

    @Schema(description = "发证机关")
    private String issuingAuthority;

    @Schema(description = "执业证书有效期至", example = "2030-12-31")
    private LocalDate licenseExpireDate;

    @Schema(description = "行政职务")
    private String position;

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
}
