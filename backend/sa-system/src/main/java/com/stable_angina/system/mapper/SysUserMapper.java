package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.system.domain.dto.SysUserQueryDTO;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.domain.vo.SysUserStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 账号分页：用户名 / 姓名 / 手机号分别模糊匹配，角色与状态精确过滤
     */
    Page<SysUser> selectUserPage(Page<SysUser> page, @Param("query") SysUserQueryDTO query);

    /**
     * 账号统计：总数 / 启用 / 停用 / 医生角色
     */
    SysUserStatsVO selectStats();
}
