package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.system.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 按用户查询权限标识（去重、非空、已启用）
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
