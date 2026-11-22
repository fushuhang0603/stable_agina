package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.business.domain.entity.PatientChangeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者档案变更留痕
 */
@Mapper
public interface PatientChangeLogMapper extends BaseMapper<PatientChangeLog> {
}
