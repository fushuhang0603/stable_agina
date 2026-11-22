package com.stable_angina.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型行数据
 */
@Data
@Schema(description = "字典类型行数据")
public class SysDictTypeVO implements Serializable {

    @Schema(description = "字典类型主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型编码")
    private String dictType;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "状态文本：启用 / 停用")
    private String statusText;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "字典项数量")
    private Long itemCount;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
