package com.stable_angina.system.controller.admin;

import com.stable_angina.common.api.Result;
import com.stable_angina.system.domain.dto.SysDictDataCreateDTO;
import com.stable_angina.system.domain.dto.SysDictDataUpdateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeCreateDTO;
import com.stable_angina.system.domain.dto.SysDictTypeUpdateDTO;
import com.stable_angina.system.domain.vo.SysDictDataVO;
import com.stable_angina.system.domain.vo.SysDictTypeVO;
import com.stable_angina.system.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 基础字典接口，完整路径 = /stableanginaapi/api/system/dicts/**
 */
@Tag(name = "基础字典")
@RestController
@RequestMapping("/api/system/dicts")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    @Operation(summary = "字典类型列表")
    @GetMapping
    public Result<List<SysDictTypeVO>> typeList() {
        return Result.success(sysDictService.typeList());
    }

    @Operation(summary = "字典项列表（按字典类型编码）")
    @GetMapping("/{dictType}/items")
    public Result<List<SysDictDataVO>> itemList(@PathVariable String dictType) {
        return Result.success(sysDictService.itemList(dictType));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping
    public Result<Void> createType(@Valid @RequestBody SysDictTypeCreateDTO dto) {
        sysDictService.createType(dto);
        return Result.success();
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/{id}")
    public Result<Void> updateType(@PathVariable Long id, @Valid @RequestBody SysDictTypeUpdateDTO dto) {
        sysDictService.updateType(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{id}")
    public Result<Void> deleteType(@PathVariable Long id) {
        sysDictService.deleteType(id);
        return Result.success();
    }

    @Operation(summary = "新增字典项")
    @PostMapping("/items")
    public Result<Void> createItem(@Valid @RequestBody SysDictDataCreateDTO dto) {
        sysDictService.createItem(dto);
        return Result.success();
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/items/{id}")
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody SysDictDataUpdateDTO dto) {
        sysDictService.updateItem(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/items/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        sysDictService.deleteItem(id);
        return Result.success();
    }
}
