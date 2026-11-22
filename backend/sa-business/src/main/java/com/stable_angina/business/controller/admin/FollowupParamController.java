package com.stable_angina.business.controller.admin;

import com.stable_angina.business.domain.dto.FollowupParamCreateDTO;
import com.stable_angina.business.domain.dto.FollowupParamQueryDTO;
import com.stable_angina.business.domain.dto.FollowupParamUpdateDTO;
import com.stable_angina.business.domain.vo.FollowupParamVO;
import com.stable_angina.business.service.FollowupParamService;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.api.Result;
import com.stable_angina.system.annotation.OperLog;
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

@Tag(name = "随访参数")
@RestController
@RequestMapping("/api/business/followup-params")
public class FollowupParamController {

    @Autowired
    private FollowupParamService followupParamService;

    @Operation(summary = "分页查询随访参数")
    @GetMapping
    public Result<PageResult<FollowupParamVO>> page(FollowupParamQueryDTO query) {
        return Result.success(followupParamService.page(query));
    }

    @Operation(summary = "参数分组列表")
    @GetMapping("/groups")
    public Result<List<String>> groups() {
        return Result.success(followupParamService.groups());
    }

    @Operation(summary = "新增随访参数")
    @OperLog(module = "随访参数", action = "新增参数")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody FollowupParamCreateDTO dto) {
        followupParamService.create(dto);
        return Result.success();
    }

    @Operation(summary = "修改随访参数")
    @OperLog(module = "随访参数", action = "修改参数")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FollowupParamUpdateDTO dto) {
        followupParamService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除随访参数")
    @OperLog(module = "随访参数", action = "删除参数")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        followupParamService.delete(id);
        return Result.success();
    }
}
