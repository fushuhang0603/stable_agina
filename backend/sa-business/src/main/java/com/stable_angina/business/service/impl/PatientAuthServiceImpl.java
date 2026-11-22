package com.stable_angina.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stable_angina.business.constant.PatientConstants;
import com.stable_angina.business.domain.dto.PatientRegisterDTO;
import com.stable_angina.business.domain.entity.Patient;
import com.stable_angina.business.domain.vo.PatientLoginVO;
import com.stable_angina.business.domain.vo.PatientProfileVO;
import com.stable_angina.business.mapper.PatientMapper;
import com.stable_angina.business.service.PatientAuthService;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.constant.AuthConstants;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.domain.dto.LoginDTO;
import com.stable_angina.system.domain.entity.SysLoginLog;
import com.stable_angina.system.domain.entity.SysRole;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.domain.entity.SysUserRole;
import com.stable_angina.system.mapper.SysLoginLogMapper;
import com.stable_angina.system.mapper.SysRoleMapper;
import com.stable_angina.system.mapper.SysUserMapper;
import com.stable_angina.system.mapper.SysUserRoleMapper;
import com.stable_angina.system.security.JwtTokenProvider;
import com.stable_angina.system.security.LoginUser;
import com.stable_angina.system.security.SecurityUtils;
import com.stable_angina.system.security.TokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PatientAuthServiceImpl implements PatientAuthService {

    /** 身份证校验权重 */
    private static final int[] ID_CARD_WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    /** 身份证校验码 */
    private static final char[] ID_CARD_CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    /** 患者编号日期段格式 */
    private static final DateTimeFormatter PATIENT_NO_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    /** 身份证出生日期段格式 */
    private static final DateTimeFormatter ID_CARD_BIRTH = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysLoginLogMapper loginLogMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private PatientMapper patientMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientLoginVO register(PatientRegisterDTO dto, String ip, String userAgent) {
        String username = dto.getUsername().trim();
        String realName = dto.getRealName().trim();
        String phone = dto.getPhone().trim();
        String idCard = dto.getIdCard().trim().toUpperCase();

        validatePassword(dto.getPassword());
        validateIdCard(idCard);
        if (!phone.matches("1\\d{10}")) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "手机号格式不正确");
        }

        LambdaQueryWrapper<SysUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUser::getUsername, username);
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
        }

        LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(SysUser::getPhone, phone);
        if (userMapper.selectCount(phoneWrapper) > 0) {
            throw new BusinessException(ErrorCodes.PHONE_EXISTS, "手机号已注册");
        }

        LambdaQueryWrapper<SysUser> idCardWrapper = new LambdaQueryWrapper<>();
        idCardWrapper.eq(SysUser::getIdCard, idCard);
        if (userMapper.selectCount(idCardWrapper) > 0) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该身份证号已注册");
        }

        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, AuthConstants.ROLE_PATIENT);
        roleWrapper.eq(SysRole::getStatus, CommonConstants.STATUS_ENABLED);
        SysRole role = roleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者角色不存在或已停用");
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setGender(readGender(idCard));
        user.setPhone(phone);
        user.setStatus(CommonConstants.STATUS_ENABLED);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);

        Patient patient = new Patient();
        patient.setUserId(user.getId());
        patient.setPatientNo(generatePatientNo());
        patient.setName(realName);
        patient.setGender(user.getGender());
        patient.setBirthDate(readBirthDate(idCard));
        patient.setPhone(phone);
        patient.setRiskLevel(PatientConstants.RISK_LOW);
        patient.setStatus(PatientConstants.PATIENT_STATUS_FOLLOWING);
        patient.setCreateTime(now);
        patient.setUpdateTime(now);
        patientMapper.insert(patient);

        log.info("患者注册成功: userId={}, patientId={}, username={}", user.getId(), patient.getId(), username);
        return issueToken(user, List.of(role), ip, userAgent, "注册成功");
    }

    @Override
    public PatientLoginVO login(LoginDTO dto, String ip, String userAgent) {
        String username = dto.getUsername().trim();

        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(userWrapper);
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            saveLoginLog(username, ip, userAgent, 0, "账号或密码错误");
            throw new BusinessException(ErrorCodes.BAD_CREDENTIALS, "账号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != CommonConstants.STATUS_ENABLED) {
            saveLoginLog(username, ip, userAgent, 0, "账号已停用");
            throw new BusinessException(ErrorCodes.USER_DISABLED, "账号已停用，请联系管理员");
        }

        List<SysRole> roles = roleMapper.selectEnabledByUserId(user.getId());
        boolean patientRole = roles.stream()
                .anyMatch(role -> AuthConstants.ROLE_PATIENT.equals(role.getRoleKey()));
        if (!patientRole) {
            saveLoginLog(username, ip, userAgent, 0, "非患者账号");
            throw new BusinessException(ErrorCodes.FORBIDDEN, "该账号不是患者账号");
        }
        return issueToken(user, roles, ip, userAgent, "登录成功");
    }

    @Override
    public PatientProfileVO profile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
        }
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(Patient::getUserId, userId);
        Patient patient = patientMapper.selectOne(patientWrapper);
        if (patient == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在");
        }
        return loadProfile(patient.getId());
    }

    private PatientLoginVO issueToken(SysUser user, List<SysRole> roles, String ip, String userAgent, String logMsg) {
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(Patient::getUserId, user.getId());
        Patient patient = patientMapper.selectOne(patientWrapper);
        if (patient == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在，请联系管理员");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setOrg("");
        loginUser.setRoles(roles.stream().map(SysRole::getRoleKey).toList());
        loginUser.setPermissions(List.of());

        String token = tokenProvider.createToken(loginUser);
        tokenStore.save(loginUser);

        LambdaUpdateWrapper<SysUser> loginWrapper = new LambdaUpdateWrapper<>();
        loginWrapper.set(SysUser::getLastLoginTime, LocalDateTime.now());
        loginWrapper.set(SysUser::getLastLoginIp, ip);
        loginWrapper.eq(SysUser::getId, user.getId());
        userMapper.update(null, loginWrapper);

        saveLoginLog(user.getUsername(), ip, userAgent, 1, logMsg);
        return new PatientLoginVO(token, tokenProvider.getTtl().toSeconds(), loadProfile(patient.getId()));
    }

    private PatientProfileVO loadProfile(Long patientId) {
        PatientProfileVO profile = patientMapper.selectPatientProfile(patientId);
        if (profile == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在");
        }
        Integer gender = profile.getGender();
        profile.setGenderText(gender == null ? "未知" : switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        });
        profile.setRiskLevelText(PatientConstants.riskLevelText(profile.getRiskLevel()));
        profile.setStatusText(PatientConstants.statusText(profile.getStatus()));
        profile.setIdCard(maskIdCard(profile.getIdCard()));
        return profile;
    }

    private void saveLoginLog(String username, String ip, String userAgent, int status, String msg) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setIp(ip);
        loginLog.setUserAgent(userAgent);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(loginLog);
    }

    private String generatePatientNo() {
        for (int i = 0; i < 5; i++) {
            String patientNo = "P" + LocalDate.now().format(PATIENT_NO_DATE)
                    + String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
            LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Patient::getPatientNo, patientNo);
            if (patientMapper.selectCount(wrapper) == 0) {
                return patientNo;
            }
        }
        throw new BusinessException(ErrorCodes.SERVER_ERROR, "患者编号生成失败，请重试");
    }

    private void validatePassword(String password) {
        if (password.length() < 6 || password.length() > 32) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "密码长度需为 6~32 位");
        }
    }

    private void validateIdCard(String idCard) {
        if (!idCard.matches("\\d{17}[\\dX]")) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "身份证号格式不正确");
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * ID_CARD_WEIGHTS[i];
        }
        char expected = ID_CARD_CHECK_CODES[sum % 11];
        if (expected != idCard.charAt(17)) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "身份证号校验位不正确");
        }
        LocalDate birthDate = readBirthDate(idCard);
        if (birthDate.isAfter(LocalDate.now())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "身份证号出生日期不正确");
        }
    }

    private LocalDate readBirthDate(String idCard) {
        try {
            return LocalDate.parse(idCard.substring(6, 14), ID_CARD_BIRTH);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "身份证号出生日期不正确");
        }
    }

    /**
     * 身份证第 17 位奇数为男、偶数为女
     */
    private Integer readGender(String idCard) {
        int sequence = idCard.charAt(16) - '0';
        return sequence % 2 == 1 ? 1 : 2;
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(14);
    }
}
