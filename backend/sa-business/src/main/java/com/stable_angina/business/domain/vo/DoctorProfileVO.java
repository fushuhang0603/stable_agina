package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生档案行数据 / 详情
 */
@Data
@Schema(description = "医生档案")
public class DoctorProfileVO implements Serializable {

    @Schema(description = "档案主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "账号主键（sys_user.id）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "所属医院")
    private String hospital;

    @Schema(description = "所属科室")
    private String department;

    @Schema(description = "行政职务")
    private String position;

    @Schema(description = "擅长领域")
    private String specialty;

    @Schema(description = "执业证书编号")
    private String licenseNo;

    @Schema(description = "执业类别")
    private String licenseType;

    @Schema(description = "执业范围")
    private String practiceScope;

    @Schema(description = "资格证书编号")
    private String certNo;

    @Schema(description = "发证机关")
    private String issuingAuthority;

    @Schema(description = "执业证书有效期至")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate licenseExpireDate;

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

    @Schema(description = "是否已建档：true 已建档 false 未建档")
    private Boolean hasProfile;

    @Schema(description = "账号状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "账号状态文本：启用 / 停用")
    private String statusText;

    @Schema(description = "档案更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
