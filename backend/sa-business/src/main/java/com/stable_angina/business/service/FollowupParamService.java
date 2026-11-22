package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.FollowupParamCreateDTO;
import com.stable_angina.business.domain.dto.FollowupParamQueryDTO;
import com.stable_angina.business.domain.dto.FollowupParamUpdateDTO;
import com.stable_angina.business.domain.vo.FollowupParamVO;
import com.stable_angina.common.api.PageResult;

import java.util.List;

/**
 * 随访参数
 */
public interface FollowupParamService {

    PageResult<FollowupParamVO> page(FollowupParamQueryDTO query);

    /**
     * 参数分组列表
     */
    List<String> groups();

    void create(FollowupParamCreateDTO dto);

    void update(Long id, FollowupParamUpdateDTO dto);

    void delete(Long id);
}
