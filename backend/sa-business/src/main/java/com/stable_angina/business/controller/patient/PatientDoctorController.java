package com.stable_angina.business.controller.patient;

import com.stable_angina.business.domain.dto.DoctorApplySubmitDTO;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.business.domain.vo.PatientDoctorOptionVO;
import com.stable_angina.business.service.PatientDoctorService;
import com.stable_angina.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端我的医生接口，完整路径 = /stableanginaapi/api/patient/**
 */
@Tag(name = "患者端我的医生")
@RestController
@RequestMapping("/api/patient")
public class PatientDoctorController {

    @Autowired
    private PatientDoctorService patientDoctorService;

    @Operation(summary = "可绑定医生列表")
    @GetMapping("/doctors")
    public Result<List<PatientDoctorOptionVO>> doctors() {
        return Result.success(patientDoctorService.listDoctors());
    }

    @Operation(summary = "当前绑定医生")
    @GetMapping("/doctor/current")
    public Result<PatientDoctorOptionVO> currentDoctor() {
        return Result.success(patientDoctorService.currentDoctor());
    }

    @Operation(summary = "当前待审核绑定申请")
    @GetMapping("/doctor-apply/current")
    public Result<PatientDoctorApplyVO> currentApply() {
        return Result.success(patientDoctorService.currentApply());
    }

    @Operation(summary = "提交绑定医生申请")
    @PostMapping("/doctor-apply")
    public Result<Void> submitApply(@Valid @RequestBody DoctorApplySubmitDTO dto) {
        patientDoctorService.submitApply(dto);
        return Result.success();
    }

    @Operation(summary = "取消绑定申请")
    @PostMapping("/doctor-apply/{id}/cancel")
    public Result<Void> cancelApply(@PathVariable Long id) {
        patientDoctorService.cancelApply(id);
        return Result.success();
    }
}
