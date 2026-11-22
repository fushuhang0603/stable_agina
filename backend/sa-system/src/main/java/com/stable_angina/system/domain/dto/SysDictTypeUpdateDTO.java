package com.stable_angina.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改字典类型入参
 */
@Data
@Schema(description = "修改字典类型入参")
public class SysDictTypeUpdateDTO {

    @Schema(description = "字典名称", example = "随访类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请输入字典名称")
    private String dictName;

    @Schema(description = "状态：1 正常 0 停用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
