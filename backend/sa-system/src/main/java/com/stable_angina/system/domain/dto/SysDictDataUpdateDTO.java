package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改字典项入参
 */
@Data
@Schema(description = "修改字典项入参")
public class SysDictDataUpdateDTO {

    @Schema(description = "字典标签", example = "劳累", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入字典标签")
    private String dictLabel;

    @Schema(description = "字典键值", example = "exertion", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入字典键值")
    private String dictValue;

    @Schema(description = "排序，升序", example = "1")
    private Integer sort;

    @Schema(description = "状态：1 正常 0 停用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
