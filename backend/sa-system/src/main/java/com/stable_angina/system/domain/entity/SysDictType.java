package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
@Schema(description = "字典类型")
public class SysDictType extends BaseEntity {

    @Schema(description = "字典类型主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型编码")
    private String dictType;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
