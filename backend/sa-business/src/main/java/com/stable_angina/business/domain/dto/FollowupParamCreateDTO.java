package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增随访参数入参
 */
@Data
@Schema(description = "新增随访参数入参")
public class FollowupParamCreateDTO {

    @Schema(description = "参数键，全局唯一", example = "bp.sbp.threshold", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入参数键")
    private String paramKey;

    @Schema(description = "参数名称", example = "收缩压告警阈值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入参数名称")
    private String paramName;

    @Schema(description = "参数分组", example = "血压告警", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入参数分组")
    private String paramGroup;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "值类型：NUMBER / TEXT / BOOLEAN", example = "NUMBER")
    private String valueType;

    @Schema(description = "参数说明")
    private String description;

    @Schema(description = "分组内排序")
    private Integer sort;

    @Schema(description = "状态：1 启用 0 停用")
    private Integer status;
}
