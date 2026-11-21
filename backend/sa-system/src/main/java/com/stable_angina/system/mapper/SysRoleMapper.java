package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.system.domain.entity.SysRole;
import com.stable_angina.system.domain.vo.SysRoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 按用户查询已启用角色（role_key + data_scope）
     */
    List<SysRole> selectEnabledByUserId(@Param("userId") Long userId);

    /**
     * 角色列表（含绑定用户数、已授权菜单数）
     */
    List<SysRoleVO> selectRoleListWithUserCount();

    /**
     * 查询角色已授权的菜单 ID
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
