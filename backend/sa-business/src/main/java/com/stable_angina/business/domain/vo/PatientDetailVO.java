package com.stable_angina.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 患者档案详情：主体 + 基线 + 变更留痕
 */
@Data
@Schema(description = "患者档案详情")
public class PatientDetailVO implements Serializable {

    @Schema(description = "患者档案主体")
    private PatientVO patient;

    @Schema(description = "基线档案")
    private PatientBaselineVO baseline;

    @Schema(description = "档案变更留痕，按时间倒序")
    private List<PatientChangeLogVO> changeLogs;
}
