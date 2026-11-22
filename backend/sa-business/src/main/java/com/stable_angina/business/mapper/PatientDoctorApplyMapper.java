package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.DoctorApplyQueryDTO;
import com.stable_angina.business.domain.entity.PatientDoctorApply;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 患者绑定医生申请
 */
@Mapper
public interface PatientDoctorApplyMapper extends BaseMapper<PatientDoctorApply> {

    /**
     * 绑定申请分页：患者姓名 / 医生姓名分别模糊匹配
     */
    Page<PatientDoctorApplyVO> selectApplyPage(Page<PatientDoctorApplyVO> page,
                                               @Param("query") DoctorApplyQueryDTO query,
                                               @Param("doctorId") Long doctorId);

    PatientDoctorApplyVO selectApplyById(@Param("id") Long id);
}
