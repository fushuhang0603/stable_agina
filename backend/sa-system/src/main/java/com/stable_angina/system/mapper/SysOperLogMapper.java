package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.system.domain.dto.SysOperLogQueryDTO;
import com.stable_angina.system.domain.entity.SysOperLog;
import com.stable_angina.system.domain.vo.SysOperLogStatsVO;
import com.stable_angina.system.domain.vo.SysOperLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 分页查询操作日志
     */
    IPage<SysOperLogVO> selectOperLogPage(Page<SysOperLogVO> page, @Param("query") SysOperLogQueryDTO query);

    /**
     * 今日操作日志统计
     */
    SysOperLogStatsVO selectStats();
}
