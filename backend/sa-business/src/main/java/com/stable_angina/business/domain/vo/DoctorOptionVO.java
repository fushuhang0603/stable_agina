package com.stable_angina.business.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 医生下拉选项
 */
@Data
@Schema(description = "医生下拉选项")
public class DoctorOptionVO implements Serializable {

    @Schema(description = "医生账号主键（sys_user.id）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "医生姓名")
    private String name;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "所属科室")
    private String department;
}
