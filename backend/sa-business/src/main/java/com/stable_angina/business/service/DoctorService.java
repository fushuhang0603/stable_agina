package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.DoctorProfileQueryDTO;
import com.stable_angina.business.domain.dto.DoctorProfileUpdateDTO;
import com.stable_angina.business.domain.vo.DoctorProfileStatsVO;
import com.stable_angina.business.domain.vo.DoctorProfileVO;
import com.stable_angina.common.api.PageResult;

/**
 * 医生档案
 */
public interface DoctorService {

    PageResult<DoctorProfileVO> page(DoctorProfileQueryDTO query);

    DoctorProfileStatsVO stats();

    DoctorProfileVO detail(Long userId);

    /**
     * 按医生档案主键查询档案明细
     */
    DoctorProfileVO detailById(Long id);

    void update(Long userId, DoctorProfileUpdateDTO dto);
}
