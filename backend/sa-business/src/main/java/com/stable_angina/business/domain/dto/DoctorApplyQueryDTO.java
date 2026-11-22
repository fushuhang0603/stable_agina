package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 医生绑定申请分页查询入参
 */
@Data
@Schema(description = "医生绑定申请分页查询入参")
public class DoctorApplyQueryDTO {

    @Schema(description = "状态：1 待审核 2 已通过 3 已驳回 4 已取消")
    private Integer status;

    @Schema(description = "申请类型：1 首次绑定 2 换绑")
    private Integer applyType;

    @Schema(description = "患者姓名（模糊匹配）")
    private String patientName;

    @Schema(description = "医生姓名（模糊匹配）")
    private String doctorName;

    @Schema(description = "页码，从 1 开始")
    private long pageNum = 1;

    @Schema(description = "每页条数")
    private long pageSize = 10;
}
