package com.stable_angina.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
@Schema(description = "字典数据")
public class SysDictData extends BaseEntity {

    @Schema(description = "字典数据主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "字典类型编码")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "排序，升序")
    private Integer sort;

    @Schema(description = "样式类")
    private String cssClass;

    @Schema(description = "是否默认：1 是 0 否")
    private Integer isDefault;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
