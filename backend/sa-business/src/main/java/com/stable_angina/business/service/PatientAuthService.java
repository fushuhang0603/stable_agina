package com.stable_angina.business.service;

import com.stable_angina.business.domain.dto.PatientRegisterDTO;
import com.stable_angina.business.domain.vo.PatientLoginVO;
import com.stable_angina.business.domain.vo.PatientProfileVO;
import com.stable_angina.system.domain.dto.LoginDTO;

/**
 * 患者端账号：注册、登录、本人档案
 */
public interface PatientAuthService {

    /**
     * 注册即建档，注册成功后直接签发令牌
     */
    PatientLoginVO register(PatientRegisterDTO dto, String ip, String userAgent);

    /**
     * 仅允许 PATIENT 角色登录
     */
    PatientLoginVO login(LoginDTO dto, String ip, String userAgent);

    /**
     * 当前登录患者的档案
     */
    PatientProfileVO profile();
}
