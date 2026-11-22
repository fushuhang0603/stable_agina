package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者档案行数据 / 详情主体
 */
@Data
@Schema(description = "患者档案")
public class PatientVO implements Serializable {

    @Schema(description = "患者主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "患者编号")
    private String patientNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别：0 未知 1 男 2 女")
    private Integer gender;

    @Schema(description = "性别文本")
    private String genderText;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthDate;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "确诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate diagnoseDate;

    @Schema(description = "过敏史")
    private String allergy;

    @Schema(description = "合并症")
    private String comorbidity;

    @Schema(description = "危险分层：HIGH / MID / LOW")
    private String riskLevel;

    @Schema(description = "危险分层文本")
    private String riskLevelText;

    @Schema(description = "随访状态：FOLLOWING / OVERDUE")
    private String status;

    @Schema(description = "随访状态文本")
    private String statusText;

    @Schema(description = "管床医生（sys_user.id）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long doctorId;

    @Schema(description = "管床医生姓名")
    private String doctorName;

    @Schema(description = "最近随访日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate lastVisitDate;

    @Schema(description = "下次随访日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate nextVisitDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "档案更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
