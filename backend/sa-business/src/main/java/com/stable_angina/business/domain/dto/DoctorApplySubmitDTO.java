package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 患者提交绑定医生申请入参
 */
@Data
@Schema(description = "患者提交绑定医生申请入参")
public class DoctorApplySubmitDTO {

    @Schema(description = "目标医生 sys_user.id")
    @NotNull(message = "请选择医生")
    private Long doctorId;

    @Schema(description = "申请说明")
    @NotBlank(message = "请填写申请说明")
    private String applyReason;
}
