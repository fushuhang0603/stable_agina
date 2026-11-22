package com.stable_angina.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.FollowupParamCreateDTO;
import com.stable_angina.business.domain.dto.FollowupParamQueryDTO;
import com.stable_angina.business.domain.dto.FollowupParamUpdateDTO;
import com.stable_angina.business.domain.entity.FollowupParam;
import com.stable_angina.business.domain.vo.FollowupParamVO;
import com.stable_angina.business.mapper.FollowupParamMapper;
import com.stable_angina.business.service.FollowupParamService;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FollowupParamServiceImpl implements FollowupParamService {

    @Autowired
    private FollowupParamMapper followupParamMapper;

    @Override
    public PageResult<FollowupParamVO> page(FollowupParamQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);

        Page<FollowupParamVO> voPage = followupParamMapper.selectParamPage(new Page<>(current, size), query);
        List<FollowupParamVO> rows = voPage.getRecords();
        rows.forEach(vo -> vo.setStatusText(statusText(vo.getStatus())));

        PageResult<FollowupParamVO> result = new PageResult<>();
        result.setRecords(rows);
        result.setTotal(voPage.getTotal());
        result.setPageNum(current);
        result.setPageSize(size);
        return result;
    }

    @Override
    public List<String> groups() {
        return followupParamMapper.selectGroups();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FollowupParamCreateDTO dto) {
        if (followupParamMapper.countByParamKey(dto.getParamKey(), null) > 0) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "参数键已存在");
        }
        FollowupParam entity = BeanUtil.copyProperties(dto, FollowupParam.class);
        if (!StringUtils.hasText(entity.getValueType())) {
            entity.setValueType("TEXT");
        }
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(CommonConstants.STATUS_ENABLED);
        }
        Long operatorId = SecurityUtils.getUserId();
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateBy(operatorId);
        entity.setCreateTime(now);
        entity.setUpdateBy(operatorId);
        entity.setUpdateTime(now);
        followupParamMapper.insert(entity);
        log.info("新增随访参数: paramKey={}, paramName={}", entity.getParamKey(), entity.getParamName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FollowupParamUpdateDTO dto) {
        if (followupParamMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "随访参数不存在");
        }
        FollowupParam update = BeanUtil.copyProperties(dto, FollowupParam.class);
        update.setId(id);
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        followupParamMapper.updateById(update);
        log.info("修改随访参数: id={}, paramName={}", id, dto.getParamName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (followupParamMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "随访参数不存在");
        }
        followupParamMapper.deleteById(id);
        log.info("删除随访参数: id={}", id);
    }

    private String statusText(Integer status) {
        return status != null && status == CommonConstants.STATUS_ENABLED ? "启用" : "停用";
    }
}
