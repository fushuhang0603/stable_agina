package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.business.domain.entity.PatientBaseline;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者基线档案
 */
@Mapper
public interface PatientBaselineMapper extends BaseMapper<PatientBaseline> {
}
