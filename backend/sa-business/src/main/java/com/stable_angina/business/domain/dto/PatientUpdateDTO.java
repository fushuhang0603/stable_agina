package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 编辑患者档案入参
 */
@Data
@Schema(description = "编辑患者档案入参")
public class PatientUpdateDTO {

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入姓名")
    private String name;

    @Schema(description = "性别：0 未知 1 男 2 女")
    private Integer gender;

    @Schema(description = "出生日期", example = "1964-05-08")
    private LocalDate birthDate;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "过敏史")
    private String allergy;

    @Schema(description = "合并症")
    private String comorbidity;

    @Schema(description = "危险分层：HIGH / MID / LOW")
    private String riskLevel;

    @Schema(description = "随访状态：FOLLOWING / OVERDUE")
    private String status;

    @Schema(description = "下次随访日期", example = "2026-12-28")
    private LocalDate nextVisitDate;

    @Schema(description = "备注")
    private String remark;
}
