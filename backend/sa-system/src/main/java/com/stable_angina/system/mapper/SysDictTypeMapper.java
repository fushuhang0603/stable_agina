package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.system.domain.entity.SysDictType;
import com.stable_angina.system.domain.vo.SysDictTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 字典类型列表（含字典项数量）
     */
    List<SysDictTypeVO> selectTypeListWithItemCount();

    /**
     * 按字典编码统计（含已逻辑删除记录，用于唯一性校验）
     */
    Long countByDictType(@Param("dictType") String dictType);
}
