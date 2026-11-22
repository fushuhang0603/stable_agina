package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 患者档案分页查询入参
 */
@Data
@Schema(description = "患者档案分页查询入参")
public class PatientQueryDTO {

    @Schema(description = "姓名（模糊匹配）")
    private String name;

    @Schema(description = "患者编号（模糊匹配）")
    private String patientNo;

    @Schema(description = "手机号（模糊匹配）")
    private String phone;

    @Schema(description = "危险分层：1 低危 2 中危 3 高危")
    private Integer riskLevel;

    @Schema(description = "随访状态：FOLLOWING / OVERDUE")
    private String status;

    @Schema(description = "管床医生（sys_user.id）")
    private Long doctorId;

    @Schema(description = "页码，从 1 开始")
    private long pageNum = 1;

    @Schema(description = "每页条数")
    private long pageSize = 10;
}
