package com.stable_angina.business.mapper;

import com.stable_angina.business.domain.vo.PatientDoctorOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 患者端医生信息查询
 */
@Mapper
public interface PatientDoctorMapper {

    /**
     * 可绑定医生列表：具备 DOCTOR 角色且账号启用
     */
    List<PatientDoctorOptionVO> selectDoctorOptions();

    /**
     * 单个可绑定医生，非医生角色或已停用返回 null
     */
    PatientDoctorOptionVO selectDoctorById(@Param("doctorId") Long doctorId);
}
