package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 随访参数分页查询入参
 */
@Data
@Schema(description = "随访参数分页查询入参")
public class FollowupParamQueryDTO {

    @Schema(description = "参数键（模糊匹配）")
    private String paramKey;

    @Schema(description = "参数名称（模糊匹配）")
    private String paramName;

    @Schema(description = "参数分组")
    private String paramGroup;

    @Schema(description = "状态：1 启用 0 停用")
    private Integer status;

    @Schema(description = "页码，从 1 开始")
    private long pageNum = 1;

    @Schema(description = "每页条数")
    private long pageSize = 10;
}
