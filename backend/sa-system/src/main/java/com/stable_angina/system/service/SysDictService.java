package com.stable_angina.system.service;

import com.stable_angina.system.domain.dto.SysDictDataCreateDTO;
import com.stable_angina.system.domain.dto.SysDictDataUpdateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeCreateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeUpdateDTO;
import com.stable_angina.system.domain.vo.SysDictDataVO;
import com.stable_angina.system.domain.vo.SysDictTypeVO;

import java.util.List;

/**
 * 基础字典服务
 */
public interface SysDictService {

    /**
     * 字典类型列表（含字典项数量）
     */
    List<SysDictTypeVO> typeList();

    /**
     * 按字典类型编码查询字典项
     */
    List<SysDictDataVO> itemList(String dictType);

    /**
     * 新增字典类型
     */
    void createType(SysDictTypeCreateDTO dto);

    /**
     * 修改字典类型
     */
    void updateType(Long id, SysDictTypeUpdateDTO dto);

    /**
     * 删除字典类型及其字典项
     */
    void deleteType(Long id);

    /**
     * 新增字典项
     */
    void createItem(SysDictDataCreateDTO dto);

    /**
     * 修改字典项
     */
    void updateItem(Long id, SysDictDataUpdateDTO dto);

    /**
     * 删除字典项
     */
    void deleteItem(Long id);
}
