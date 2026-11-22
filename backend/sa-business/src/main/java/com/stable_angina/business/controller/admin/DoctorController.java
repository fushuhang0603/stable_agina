package com.stable_angina.business.controller.admin;

import com.stable_angina.business.domain.dto.DoctorProfileQueryDTO;
import com.stable_angina.business.domain.dto.DoctorProfileUpdateDTO;
import com.stable_angina.business.domain.vo.DoctorProfileStatsVO;
import com.stable_angina.business.domain.vo.DoctorProfileVO;
import com.stable_angina.business.service.DoctorService;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.api.Result;
import com.stable_angina.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "医生管理")
@RestController
@RequestMapping("/api/business/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Operation(summary = "分页查询医生列表")
    @GetMapping
    public Result<PageResult<DoctorProfileVO>> page(DoctorProfileQueryDTO query) {
        return Result.success(doctorService.page(query));
    }

    @Operation(summary = "医生档案统计")
    @GetMapping("/stats")
    public Result<DoctorProfileStatsVO> stats() {
        return Result.success(doctorService.stats());
    }

    @Operation(summary = "医生档案明细（按档案主键）")
    @GetMapping("/profile")
    public Result<DoctorProfileVO> profileDetail(@RequestParam Long profileId) {
        return Result.success(doctorService.detailById(profileId));
    }

    @Operation(summary = "医生档案详情")
    @GetMapping("/{userId}")
    public Result<DoctorProfileVO> detail(@PathVariable Long userId) {
        return Result.success(doctorService.detail(userId));
    }

    @Operation(summary = "保存医生档案")
    @OperLog(module = "医生管理", action = "修改档案")
    @PutMapping("/{userId}")
    public Result<Void> update(@PathVariable Long userId, @Valid @RequestBody DoctorProfileUpdateDTO dto) {
        doctorService.update(userId, dto);
        return Result.success();
    }
}
