package com.stable_angina.system.service;

import com.stable_angina.common.api.PageResult;
import com.stable_angina.system.domain.dto.SysUserCreateDTO;
import com.stable_angina.system.domain.dto.SysUserQueryDTO;
import com.stable_angina.system.domain.dto.SysUserUpdateDTO;
import com.stable_angina.system.domain.vo.SysUserStatsVO;
import com.stable_angina.system.domain.vo.SysUserVO;

/**
 * 账号管理服务
 */
public interface SysUserService {

    /**
     * 分页查询账号列表
     */
    PageResult<SysUserVO> page(SysUserQueryDTO query);

    /**
     * 账号统计概览
     */
    SysUserStatsVO stats();

    /**
     * 新增账号：密码 BCrypt 加密，绑定角色
     */
    Long create(SysUserCreateDTO dto);

    /**
     * 编辑账号：登录账号不可改，可改角色
     */
    void update(Long id, SysUserUpdateDTO dto);

    /**
     * 启用 / 停用
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String password);
}
