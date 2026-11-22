package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作日志统计概览
 */
@Data
@Schema(description = "操作日志统计概览")
public class SysOperLogStatsVO {

    @Schema(description = "今日操作数")
    private Long todayCount;

    @Schema(description = "今日失败次数")
    private Long failedCount;

    @Schema(description = "今日涉及用户数")
    private Long userCount;
}
