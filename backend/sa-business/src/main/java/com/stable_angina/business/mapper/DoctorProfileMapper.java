package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.DoctorProfileQueryDTO;
import com.stable_angina.business.domain.entity.DoctorProfile;
import com.stable_angina.business.domain.vo.DoctorProfileStatsVO;
import com.stable_angina.business.domain.vo.DoctorProfileVO;
import com.stable_angina.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 医生档案
 */
@Mapper
public interface DoctorProfileMapper extends BaseMapper<DoctorProfile> {

    /**
     * 医生分页：仅 DOCTOR 角色账号，姓名 / 手机号 / 执业证书编号分别模糊匹配
     */
    Page<SysUser> selectDoctorPage(Page<SysUser> page, @Param("query") DoctorProfileQueryDTO query);

    DoctorProfileVO selectDoctorDetail(@Param("userId") Long userId);

    DoctorProfileStatsVO selectStats();
}
