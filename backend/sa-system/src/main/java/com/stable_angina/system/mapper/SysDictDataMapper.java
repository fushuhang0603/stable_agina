package com.stable_angina.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stable_angina.system.domain.entity.SysDictData;
import com.stable_angina.system.domain.vo.SysDictDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 按字典类型编码查询字典项，排序升序
     */
    List<SysDictDataVO> selectItemsByType(@Param("dictType") String dictType);

    /**
     * 同一字典类型下按键值统计（排除指定主键，用于唯一性校验）
     */
    Long countByTypeAndValue(@Param("dictType") String dictType,
                             @Param("dictValue") String dictValue,
                             @Param("excludeId") Long excludeId);
}
