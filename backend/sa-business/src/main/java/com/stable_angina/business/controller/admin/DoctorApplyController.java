package com.stable_angina.business.controller.admin;

import com.stable_angina.business.domain.dto.DoctorApplyHandleDTO;
import com.stable_angina.business.domain.dto.DoctorApplyQueryDTO;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.business.service.DoctorApplyService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "医生绑定审核")
@RestController
@RequestMapping("/api/business/doctor-applies")
public class DoctorApplyController {

    @Autowired
    private DoctorApplyService doctorApplyService;

    @Operation(summary = "分页查询绑定申请")
    @GetMapping
    public Result<PageResult<PatientDoctorApplyVO>> page(DoctorApplyQueryDTO query) {
        return Result.success(doctorApplyService.page(query));
    }

    @Operation(summary = "审核通过")
    @OperLog(module = "医生绑定", action = "审核通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        doctorApplyService.approve(id);
        return Result.success();
    }

    @Operation(summary = "驳回申请")
    @OperLog(module = "医生绑定", action = "驳回申请")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @Valid @RequestBody DoctorApplyHandleDTO dto) {
        doctorApplyService.reject(id, dto);
        return Result.success();
    }
}
