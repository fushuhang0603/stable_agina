package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.DoctorApplyHandleDTO;
import com.stable_angina.business.domain.dto.DoctorApplyQueryDTO;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.common.api.PageResult;

/**
 * 医生绑定申请审核
 */
public interface DoctorApplyService {

    PageResult<PatientDoctorApplyVO> page(DoctorApplyQueryDTO query);

    /**
     * 审核通过：更新患者管床医生并留痕
     */
    void approve(Long id);

    /**
     * 驳回申请
     */
    void reject(Long id, DoctorApplyHandleDTO dto);
}
