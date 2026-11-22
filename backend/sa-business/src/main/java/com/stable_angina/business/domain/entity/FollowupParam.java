package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 随访参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("followup_param")
@Schema(description = "随访参数")
public class FollowupParam extends BaseEntity {

    @Schema(description = "参数主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "参数键，全局唯一")
    private String paramKey;

    @Schema(description = "参数名称")
    private String paramName;

    @Schema(description = "参数分组")
    private String paramGroup;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "值类型：NUMBER / TEXT / BOOLEAN")
    private String valueType;

    @Schema(description = "参数说明")
    private String description;

    @Schema(description = "分组内排序")
    private Integer sort;

    @Schema(description = "状态：1 启用 0 停用")
    private Integer status;
}
