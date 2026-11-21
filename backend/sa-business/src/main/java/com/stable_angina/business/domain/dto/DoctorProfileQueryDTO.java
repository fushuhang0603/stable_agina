package com.stable_angina.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 医生档案分页查询入参
 */
@Data
@Schema(description = "医生档案分页查询入参")
public class DoctorProfileQueryDTO {

    @Schema(description = "姓名（模糊匹配）")
    private String realName;

    @Schema(description = "手机号（模糊匹配）")
    private String phone;

    @Schema(description = "执业证书编号（模糊匹配）")
    private String licenseNo;

    @Schema(description = "档案状态：1 已建档 0 未建档")
    private Integer profileStatus;

    @Schema(description = "账号状态：1 启用 0 停用")
    private Integer status;

    @Schema(description = "页码，从 1 开始")
    private long pageNum = 1;

    @Schema(description = "每页条数")
    private long pageSize = 10;
}
