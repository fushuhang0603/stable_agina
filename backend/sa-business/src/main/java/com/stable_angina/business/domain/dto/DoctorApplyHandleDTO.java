package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 驳回绑定申请入参
 */
@Data
@Schema(description = "驳回绑定申请入参")
public class DoctorApplyHandleDTO {

    @Schema(description = "驳回原因")
    @NotBlank(message = "请填写驳回原因")
    private String rejectReason;
}
