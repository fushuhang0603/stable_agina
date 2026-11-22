package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.PatientQueryDTO;
import com.stable_angina.business.domain.dto.PatientUpdateDTO;
import com.stable_angina.business.domain.vo.PatientDetailVO;
import com.stable_angina.business.domain.vo.PatientStatsVO;
import com.stable_angina.business.domain.vo.PatientVO;
import com.stable_angina.common.api.PageResult;

/**
 * 患者档案
 */
public interface PatientService {

    PageResult<PatientVO> page(PatientQueryDTO query);

    PatientStatsVO stats();

    /**
     * 患者档案详情：主体 + 基线 + 变更留痕
     */
    PatientDetailVO detail(Long id);

    void update(Long id, PatientUpdateDTO dto);
}
