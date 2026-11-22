package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.DoctorApplySubmitDTO;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.business.domain.vo.PatientDoctorOptionVO;

import java.util.List;

/**
 * 患者端我的医生
 */
public interface PatientDoctorService {

    /**
     * 可绑定医生列表，并标记当前绑定与待审核状态
     */
    List<PatientDoctorOptionVO> listDoctors();

    /**
     * 当前绑定医生，未绑定返回 null
     */
    PatientDoctorOptionVO currentDoctor();

    /**
     * 提交绑定申请
     */
    void submitApply(DoctorApplySubmitDTO dto);

    /**
     * 当前待审核申请，无则返回 null
     */
    PatientDoctorApplyVO currentApply();

    /**
     * 取消本人的待审核申请
     */
    void cancelApply(Long id);
}
