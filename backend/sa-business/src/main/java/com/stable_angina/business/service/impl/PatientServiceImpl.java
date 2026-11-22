package com.stable_angina.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.constant.PatientConstants;
import com.stable_angina.business.domain.dto.PatientQueryDTO;
import com.stable_angina.business.domain.dto.PatientUpdateDTO;
import com.stable_angina.business.domain.entity.Patient;
import com.stable_angina.business.domain.entity.PatientChangeLog;
import com.stable_angina.business.domain.vo.PatientBaselineVO;
import com.stable_angina.business.domain.vo.PatientChangeLogVO;
import com.stable_angina.business.domain.vo.PatientDetailVO;
import com.stable_angina.business.domain.vo.PatientStatsVO;
import com.stable_angina.business.domain.vo.PatientVO;
import com.stable_angina.business.mapper.PatientChangeLogMapper;
import com.stable_angina.business.mapper.PatientMapper;
import com.stable_angina.business.service.PatientService;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.AuthConstants;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.system.security.LoginUser;
import com.stable_angina.system.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private PatientChangeLogMapper changeLogMapper;

    @Override
    public PageResult<PatientVO> page(PatientQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);
        Long scopeDoctorId = currentScopeDoctorId();
        if (scopeDoctorId != null) {
            query.setDoctorId(scopeDoctorId);
        }

        Page<PatientVO> voPage = patientMapper.selectPatientPage(new Page<>(current, size), query);
        List<PatientVO> rows = voPage.getRecords();
        rows.forEach(this::fillText);

        PageResult<PatientVO> result = new PageResult<>();
        result.setRecords(rows);
        result.setTotal(voPage.getTotal());
        result.setPageNum(current);
        result.setPageSize(size);
        return result;
    }

    @Override
    public PatientStatsVO stats() {
        return patientMapper.selectStats(currentScopeDoctorId());
    }

    @Override
    public PatientDetailVO detail(Long id) {
        PatientVO patient = patientMapper.selectPatientDetail(id);
        if (patient == null) {
            log.warn("患者档案不存在: patientId={}", id);
            return null;
        }
        checkDataScope(patient.getDoctorId());
        fillText(patient);

        PatientBaselineVO baseline = patientMapper.selectBaseline(id);
        List<PatientChangeLogVO> changeLogs = patientMapper.selectChangeLogs(id);

        PatientDetailVO detail = new PatientDetailVO();
        detail.setPatient(patient);
        detail.setBaseline(baseline);
        detail.setChangeLogs(changeLogs);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PatientUpdateDTO dto) {
        Patient existing = patientMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在");
        }
        checkDataScope(existing.getDoctorId());

        LocalDateTime now = LocalDateTime.now();
        Long operatorId = SecurityUtils.getUserId();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String operatorName = loginUser == null ? "系统"
                : (loginUser.getRealName() == null ? loginUser.getUsername() : loginUser.getRealName());

        Patient update = new Patient();
        update.setId(id);
        update.setName(dto.getName().trim());
        update.setGender(dto.getGender());
        update.setBirthDate(dto.getBirthDate());
        update.setPhone(dto.getPhone());
        update.setAllergy(dto.getAllergy());
        update.setComorbidity(dto.getComorbidity());
        update.setRiskLevel(dto.getRiskLevel());
        update.setStatus(dto.getStatus());
        update.setNextVisitDate(dto.getNextVisitDate());
        update.setRemark(dto.getRemark());
        update.setUpdateBy(operatorId);
        update.setUpdateTime(now);
        patientMapper.updateById(update);

        List<PatientChangeLog> changes = new ArrayList<>();
        addChange(changes, id, "姓名", existing.getName(), update.getName(), operatorName, now);
        addChange(changes, id, "性别", genderText(existing.getGender()), genderText(update.getGender()), operatorName, now);
        addChange(changes, id, "出生日期", text(existing.getBirthDate()), text(update.getBirthDate()), operatorName, now);
        addChange(changes, id, "手机号", existing.getPhone(), update.getPhone(), operatorName, now);
        addChange(changes, id, "过敏史", existing.getAllergy(), update.getAllergy(), operatorName, now);
        addChange(changes, id, "合并症", existing.getComorbidity(), update.getComorbidity(), operatorName, now);
        addChange(changes, id, "危险分层", PatientConstants.riskLevelText(existing.getRiskLevel()), PatientConstants.riskLevelText(update.getRiskLevel()), operatorName, now);
        addChange(changes, id, "随访状态", PatientConstants.statusText(existing.getStatus()), PatientConstants.statusText(update.getStatus()), operatorName, now);
        addChange(changes, id, "下次随访", text(existing.getNextVisitDate()), text(update.getNextVisitDate()), operatorName, now);
        changes.forEach(changeLogMapper::insert);
        log.info("保存患者档案成功: patientId={}, 变更字段数={}", id, changes.size());
    }

    /**
     * 非超级管理员仅能查询本人管床患者
     */
    private void checkDataScope(Long doctorId) {
        Long scopeDoctorId = currentScopeDoctorId();
        if (scopeDoctorId != null && !Objects.equals(scopeDoctorId, doctorId)) {
            throw new BusinessException(ErrorCodes.FORBIDDEN, "无权查看该患者档案");
        }
    }

    /**
     * 数据范围：超级管理员返回 null 表示不限，其他角色返回当前登录用户 ID
     */
    private Long currentScopeDoctorId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getRoles() == null || loginUser.getRoles().contains(AuthConstants.ROLE_SUPER_ADMIN)) {
            return null;
        }
        return loginUser.getUserId();
    }

    private void fillText(PatientVO vo) {
        vo.setGenderText(genderText(vo.getGender()));
        vo.setRiskLevelText(PatientConstants.riskLevelText(vo.getRiskLevel()));
        vo.setStatusText(PatientConstants.statusText(vo.getStatus()));
    }

    private String genderText(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void addChange(List<PatientChangeLog> changes, Long patientId, String fieldName,
                           String beforeValue, String afterValue, String operatorName, LocalDateTime now) {
        String before = beforeValue == null ? "" : beforeValue;
        String after = afterValue == null ? "" : afterValue;
        if (Objects.equals(before, after)) {
            return;
        }
        PatientChangeLog change = new PatientChangeLog();
        change.setPatientId(patientId);
        change.setFieldName(fieldName);
        change.setBeforeValue(before);
        change.setAfterValue(after);
        change.setOperatorName(operatorName);
        change.setChangeTime(now);
        changes.add(change);
    }
}
