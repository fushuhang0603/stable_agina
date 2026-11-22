package com.stable_angina.system.service;

import com.stable_angina.common.api.PageResult;
import com.stable_angina.system.domain.dto.SysOperLogQueryDTO;
import com.stable_angina.system.domain.vo.SysOperLogStatsVO;
import com.stable_angina.system.domain.vo.SysOperLogVO;

/**
 * 操作日志服务
 */
public interface SysOperLogService {

    /**
     * 分页查询操作日志
     */
    PageResult<SysOperLogVO> page(SysOperLogQueryDTO query);

    /**
     * 今日操作日志统计
     */
    SysOperLogStatsVO stats();
}
