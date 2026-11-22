package com.stable_angina.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 患者档案统计概览
 */
@Data
@Schema(description = "患者档案统计概览")
public class PatientStatsVO implements Serializable {

    @Schema(description = "患者总数")
    private Long total;

    @Schema(description = "高危患者数")
    private Long highRisk;

    @Schema(description = "随访中患者数")
    private Long following;

    @Schema(description = "已逾期患者数")
    private Long overdue;
}
