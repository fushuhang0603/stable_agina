package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.DoctorProfileQueryDTO;
import com.stable_angina.business.domain.entity.DoctorProfile;
import com.stable_angina.business.domain.vo.DoctorProfileStatsVO;
import com.stable_angina.business.domain.vo.DoctorProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 医生档案
 */
@Mapper
public interface DoctorProfileMapper extends BaseMapper<DoctorProfile> {

    /**
     * 医生档案分页：以 doctor_profile 为主表，姓名 / 手机号 / 执业证书编号分别模糊匹配
     */
    Page<DoctorProfileVO> selectDoctorPage(Page<DoctorProfileVO> page, @Param("query") DoctorProfileQueryDTO query);

    DoctorProfileVO selectDoctorDetail(@Param("userId") Long userId);

    DoctorProfileStatsVO selectStats();
}
