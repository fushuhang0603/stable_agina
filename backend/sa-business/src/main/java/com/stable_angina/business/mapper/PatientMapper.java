package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.PatientQueryDTO;
import com.stable_angina.business.domain.entity.Patient;
import com.stable_angina.business.domain.vo.PatientBaselineVO;
import com.stable_angina.business.domain.vo.PatientChangeLogVO;
import com.stable_angina.business.domain.vo.PatientProfileVO;
import com.stable_angina.business.domain.vo.PatientStatsVO;
import com.stable_angina.business.domain.vo.PatientVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 患者档案
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    /**
     * 患者分页：姓名 / 患者编号 / 手机号分别模糊匹配
     */
    Page<PatientVO> selectPatientPage(Page<PatientVO> page, @Param("query") PatientQueryDTO query);

    PatientVO selectPatientDetail(@Param("id") Long id);

    PatientBaselineVO selectBaseline(@Param("patientId") Long patientId);

    List<PatientChangeLogVO> selectChangeLogs(@Param("patientId") Long patientId);

    PatientStatsVO selectStats(@Param("doctorId") Long doctorId);

    /**
     * 患者端本人档案：含账号身份证与管床医生所属医院、科室
     */
    PatientProfileVO selectPatientProfile(@Param("patientId") Long patientId);
}
