package com.stable_angina.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 医生档案统计概览
 */
@Data
@Schema(description = "医生档案统计概览")
public class DoctorProfileStatsVO implements Serializable {

    @Schema(description = "医生账号总数")
    private Long total;

    @Schema(description = "已建档医生数")
    private Long profiled;

    @Schema(description = "未建档医生数")
    private Long unprofiled;

    @Schema(description = "已设置出诊时间医生数")
    private Long scheduled;
}
