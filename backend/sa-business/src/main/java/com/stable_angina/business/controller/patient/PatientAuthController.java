package com.stable_angina.business.controller.patient;

import com.stable_angina.business.domain.dto.PatientRegisterDTO;
import com.stable_angina.business.domain.vo.PatientLoginVO;
import com.stable_angina.business.domain.vo.PatientProfileVO;
import com.stable_angina.business.service.PatientAuthService;
import com.stable_angina.common.api.Result;
import com.stable_angina.system.domain.dto.LoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端账号接口，完整路径 = /stableanginaapi/api/patient/**
 */
@Tag(name = "患者端账号")
@RestController
@RequestMapping("/api/patient")
public class PatientAuthController {

    @Autowired
    private PatientAuthService patientAuthService;

    @Operation(summary = "患者注册（注册即建档）")
    @PostMapping("/auth/register")
    public Result<PatientLoginVO> register(@Valid @RequestBody PatientRegisterDTO dto, HttpServletRequest request) {
        return Result.success(patientAuthService.register(dto, clientIp(request), request.getHeader("User-Agent")));
    }

    @Operation(summary = "患者登录")
    @PostMapping("/auth/login")
    public Result<PatientLoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        return Result.success(patientAuthService.login(dto, clientIp(request), request.getHeader("User-Agent")));
    }

    @Operation(summary = "当前患者档案")
    @GetMapping("/profile")
    public Result<PatientProfileVO> profile() {
        return Result.success(patientAuthService.profile());
    }

    /**
     * 取真实客户端 IP，兼容反向代理
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return StringUtils.hasText(realIp) ? realIp : request.getRemoteAddr();
    }
}
