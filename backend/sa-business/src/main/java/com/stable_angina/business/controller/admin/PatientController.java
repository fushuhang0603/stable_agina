package com.stable_angina.business.controller.admin;

import com.stable_angina.business.domain.dto.PatientQueryDTO;
import com.stable_angina.business.domain.dto.PatientUpdateDTO;
import com.stable_angina.business.domain.vo.PatientDetailVO;
import com.stable_angina.business.domain.vo.PatientStatsVO;
import com.stable_angina.business.domain.vo.PatientVO;
import com.stable_angina.business.service.PatientService;
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

@Tag(name = "患者管理")
@RestController
@RequestMapping("/api/business/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Operation(summary = "分页查询患者列表")
    @GetMapping
    public Result<PageResult<PatientVO>> page(PatientQueryDTO query) {
        return Result.success(patientService.page(query));
    }

    @Operation(summary = "患者档案统计")
    @GetMapping("/stats")
    public Result<PatientStatsVO> stats() {
        return Result.success(patientService.stats());
    }

    @Operation(summary = "患者档案详情")
    @GetMapping("/{id}")
    public Result<PatientDetailVO> detail(@PathVariable Long id) {
        return Result.success(patientService.detail(id));
    }

    @Operation(summary = "保存患者档案")
    @OperLog(module = "患者管理", action = "修改档案")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateDTO dto) {
        patientService.update(id, dto);
        return Result.success();
    }
}
