package com.stable_angina.system.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典项行数据
 */
@Data
@Schema(description = "字典项行数据")
public class SysDictDataVO implements Serializable {

    @Schema(description = "字典数据主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "字典类型编码")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "排序，升序")
    private Integer sort;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "状态文本：启用 / 停用")
    private String statusText;

    @Schema(description = "备注")
    private String remark;
}
