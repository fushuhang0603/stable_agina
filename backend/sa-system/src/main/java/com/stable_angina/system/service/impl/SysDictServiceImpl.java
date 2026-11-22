package com.stable_angina.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.domain.dto.SysDictDataCreateDTO;
import com.stable_angina.system.domain.dto.SysDictDataUpdateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeCreateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeUpdateDTO;
import com.stable_angina.system.domain.entity.SysDictData;
import com.stable_angina.system.domain.entity.SysDictType;
import com.stable_angina.system.domain.vo.SysDictDataVO;
import com.stable_angina.system.domain.vo.SysDictTypeVO;
import com.stable_angina.system.mapper.SysDictDataMapper;
import com.stable_angina.system.mapper.SysDictTypeMapper;
import com.stable_angina.system.security.SecurityUtils;
import com.stable_angina.system.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictTypeMapper dictTypeMapper;
    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public List<SysDictTypeVO> typeList() {
        List<SysDictTypeVO> rows = dictTypeMapper.selectTypeListWithItemCount();
        rows.forEach(row -> row.setStatusText(statusText(row.getStatus())));
        return rows;
    }

    @Override
    public List<SysDictDataVO> itemList(String dictType) {
        List<SysDictDataVO> rows = dictDataMapper.selectItemsByType(dictType);
        rows.forEach(row -> row.setStatusText(statusText(row.getStatus())));
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createType(SysDictTypeCreateDTO dto) {
        if (dictTypeMapper.countByDictType(dto.getDictType()) > 0) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典编码已存在");
        }
        SysDictType entity = BeanUtil.copyProperties(dto, SysDictType.class);
        if (entity.getStatus() == null) {
            entity.setStatus(CommonConstants.STATUS_ENABLED);
        }
        entity.setCreateBy(SecurityUtils.getUserId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUserId());
        entity.setUpdateTime(LocalDateTime.now());
        dictTypeMapper.insert(entity);
        log.info("新增字典类型: dictType={}, dictName={}", entity.getDictType(), entity.getDictName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateType(Long id, SysDictTypeUpdateDTO dto) {
        if (dictTypeMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典类型不存在");
        }
        SysDictType update = BeanUtil.copyProperties(dto, SysDictType.class);
        update.setId(id);
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        dictTypeMapper.updateById(update);
        log.info("修改字典类型: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteType(Long id) {
        SysDictType type = dictTypeMapper.selectById(id);
        if (type == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典类型不存在");
        }
        dictTypeMapper.deleteById(id);

        LambdaQueryWrapper<SysDictData> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(SysDictData::getDictType, type.getDictType());
        dictDataMapper.delete(dataWrapper);
        log.info("删除字典类型: dictType={}, 同步删除其字典项", type.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createItem(SysDictDataCreateDTO dto) {
        requireType(dto.getDictType());
        if (dictDataMapper.countByTypeAndValue(dto.getDictType(), dto.getDictValue(), null) > 0) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该字典下键值已存在");
        }
        SysDictData entity = BeanUtil.copyProperties(dto, SysDictData.class);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(CommonConstants.STATUS_ENABLED);
        }
        entity.setCreateBy(SecurityUtils.getUserId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUserId());
        entity.setUpdateTime(LocalDateTime.now());
        dictDataMapper.insert(entity);
        log.info("新增字典项: dictType={}, dictValue={}", entity.getDictType(), entity.getDictValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(Long id, SysDictDataUpdateDTO dto) {
        SysDictData data = dictDataMapper.selectById(id);
        if (data == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典项不存在");
        }
        if (dictDataMapper.countByTypeAndValue(data.getDictType(), dto.getDictValue(), id) > 0) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该字典下键值已存在");
        }
        SysDictData update = BeanUtil.copyProperties(dto, SysDictData.class);
        update.setId(id);
        update.setUpdateBy(SecurityUtils.getUserId());
        update.setUpdateTime(LocalDateTime.now());
        dictDataMapper.updateById(update);
        log.info("修改字典项: id={}, dictValue={}", id, dto.getDictValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long id) {
        if (dictDataMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典项不存在");
        }
        dictDataMapper.deleteById(id);
        log.info("删除字典项: id={}", id);
    }

    private SysDictType requireType(String dictType) {
        LambdaQueryWrapper<SysDictType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(SysDictType::getDictType, dictType);
        SysDictType type = dictTypeMapper.selectOne(typeWrapper);
        if (type == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "字典类型不存在");
        }
        return type;
    }

    private String statusText(Integer status) {
        return status != null && status == CommonConstants.STATUS_ENABLED ? "启用" : "停用";
    }
}
