package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.system.domain.dto.SysUserQueryDTO;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.domain.vo.SysUserStatsVO;
import com.stable_angina.system.domain.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 用户分页列表（关联角色，返回角色标识与名称）
     */
    List<SysUserVO> selectUserPage(@Param("query") SysUserQueryDTO query,
                                   @Param("offset") long offset,
                                   @Param("pageSize") long pageSize);

    /**
     * 用户分页总数
     */
    long countUser(@Param("query") SysUserQueryDTO query);

    /**
     * 账号统计：总数 / 启用 / 停用 / 医生角色
     */
    SysUserStatsVO selectStats();
}
