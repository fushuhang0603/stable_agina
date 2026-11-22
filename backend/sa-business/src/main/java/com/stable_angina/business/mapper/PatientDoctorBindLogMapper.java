package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.business.domain.entity.PatientDoctorBindLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者医生绑定留痕
 */
@Mapper
public interface PatientDoctorBindLogMapper extends BaseMapper<PatientDoctorBindLog> {
}
