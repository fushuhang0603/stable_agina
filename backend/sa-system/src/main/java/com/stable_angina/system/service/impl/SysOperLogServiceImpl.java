package com.stable_angina.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.domain.dto.SysOperLogQueryDTO;
import com.stable_angina.system.domain.vo.SysOperLogStatsVO;
import com.stable_angina.system.domain.vo.SysOperLogVO;
import com.stable_angina.system.mapper.SysOperLogMapper;
import com.stable_angina.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    @Override
    public PageResult<SysOperLogVO> page(SysOperLogQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);

        IPage<SysOperLogVO> logPage = operLogMapper.selectOperLogPage(new Page<>(current, size), query);
        List<SysOperLogVO> rows = logPage.getRecords();
        rows.forEach(this::fillStatusText);

        PageResult<SysOperLogVO> result = new PageResult<>();
        result.setRecords(rows);
        result.setTotal(logPage.getTotal());
        result.setPageNum(current);
        result.setPageSize(size);
        return result;
    }

    @Override
    public SysOperLogStatsVO stats() {
        return operLogMapper.selectStats();
    }

    private void fillStatusText(SysOperLogVO row) {
        row.setStatusText(row.getStatus() != null && row.getStatus() == CommonConstants.STATUS_ENABLED ? "成功" : "失败");
    }
}
