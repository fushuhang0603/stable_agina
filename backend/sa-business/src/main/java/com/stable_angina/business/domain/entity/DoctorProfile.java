package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 医生档案
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doctor_profile")
@Schema(description = "医生档案")
public class DoctorProfile extends BaseEntity {

    @Schema(description = "档案主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联 sys_user.id")
    private Long userId;

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

    @Schema(description = "执业证书有效期至")
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
