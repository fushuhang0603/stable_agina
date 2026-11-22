package com.stable_angina.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stable_angina.business.constant.PatientConstants;
import com.stable_angina.business.domain.dto.DoctorApplySubmitDTO;
import com.stable_angina.business.domain.entity.Patient;
import com.stable_angina.business.domain.entity.PatientDoctorApply;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.business.domain.vo.PatientDoctorDetailVO;
import com.stable_angina.business.domain.vo.PatientDoctorOptionVO;
import com.stable_angina.business.mapper.PatientDoctorApplyMapper;
import com.stable_angina.business.mapper.PatientDoctorMapper;
import com.stable_angina.business.mapper.PatientMapper;
import com.stable_angina.business.service.PatientDoctorService;
import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.system.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientDoctorServiceImpl implements PatientDoctorService {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private PatientDoctorMapper patientDoctorMapper;
    @Autowired
    private PatientDoctorApplyMapper applyMapper;

    @Override
    public List<PatientDoctorOptionVO> listDoctors() {
        Patient patient = currentPatient();
        List<PatientDoctorOptionVO> doctors = patientDoctorMapper.selectDoctorOptions();

        LambdaQueryWrapper<PatientDoctorApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientDoctorApply::getPatientId, patient.getId());
        wrapper.eq(PatientDoctorApply::getStatus, PatientConstants.APPLY_STATUS_PENDING);
        Set<Long> pendingDoctorIds = applyMapper.selectList(wrapper).stream()
                .map(PatientDoctorApply::getDoctorId)
                .collect(Collectors.toSet());

        doctors.forEach(doctor -> {
            doctor.setBound(Objects.equals(doctor.getId(), patient.getDoctorId()));
            doctor.setPending(pendingDoctorIds.contains(doctor.getId()));
        });
        return doctors;
    }

    @Override
    public PatientDoctorOptionVO currentDoctor() {
        Patient patient = currentPatient();
        if (patient.getDoctorId() == null) {
            return null;
        }
        return patientDoctorMapper.selectDoctorById(patient.getDoctorId());
    }

    @Override
    public PatientDoctorDetailVO doctorDetail(Long doctorId) {
        PatientDoctorDetailVO detail = patientDoctorMapper.selectDoctorDetail(doctorId);
        if (detail == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "医生不存在或已停用");
        }
        Patient patient = currentPatient();
        detail.setBound(Objects.equals(detail.getId(), patient.getDoctorId()));

        LambdaQueryWrapper<PatientDoctorApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientDoctorApply::getPatientId, patient.getId());
        wrapper.eq(PatientDoctorApply::getDoctorId, doctorId);
        wrapper.eq(PatientDoctorApply::getStatus, PatientConstants.APPLY_STATUS_PENDING);
        detail.setPending(applyMapper.selectCount(wrapper) > 0);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApply(DoctorApplySubmitDTO dto) {
        Patient patient = currentPatient();
        Long doctorId = dto.getDoctorId();

        PatientDoctorOptionVO doctor = patientDoctorMapper.selectDoctorById(doctorId);
        if (doctor == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "医生不存在或已停用");
        }
        if (Objects.equals(patient.getDoctorId(), doctorId)) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "您已绑定该医生，无需重复申请");
        }

        LambdaQueryWrapper<PatientDoctorApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientDoctorApply::getPatientId, patient.getId());
        wrapper.eq(PatientDoctorApply::getStatus, PatientConstants.APPLY_STATUS_PENDING);
        List<PatientDoctorApply> pendingApplies = applyMapper.selectList(wrapper);
        if (!pendingApplies.isEmpty()) {
            boolean sameDoctor = pendingApplies.stream()
                    .anyMatch(apply -> Objects.equals(apply.getDoctorId(), doctorId));
            if (sameDoctor) {
                throw new BusinessException(ErrorCodes.BAD_REQUEST, "您已提交该医生的绑定申请，请等待医生审核");
            }
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "您有待审核的绑定申请，请先取消后再提交");
        }

        Long userId = SecurityUtils.getUserId();
        LocalDateTime now = LocalDateTime.now();
        PatientDoctorApply apply = new PatientDoctorApply();
        apply.setPatientId(patient.getId());
        apply.setUserId(userId);
        apply.setDoctorId(doctorId);
        apply.setApplyType(patient.getDoctorId() == null ? PatientConstants.APPLY_TYPE_INIT : PatientConstants.APPLY_TYPE_CHANGE);
        apply.setStatus(PatientConstants.APPLY_STATUS_PENDING);
        apply.setApplyReason(dto.getApplyReason().trim());
        apply.setCreateBy(userId);
        apply.setCreateTime(now);
        apply.setUpdateBy(userId);
        apply.setUpdateTime(now);
        applyMapper.insert(apply);
        log.info("提交绑定医生申请成功: applyId={}, patientId={}, doctorId={}", apply.getId(), patient.getId(), doctorId);
    }

    @Override
    public PatientDoctorApplyVO currentApply() {
        Patient patient = currentPatient();

        LambdaQueryWrapper<PatientDoctorApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientDoctorApply::getPatientId, patient.getId());
        wrapper.eq(PatientDoctorApply::getStatus, PatientConstants.APPLY_STATUS_PENDING);
        wrapper.orderByDesc(PatientDoctorApply::getCreateTime);
        wrapper.orderByDesc(PatientDoctorApply::getId);
        List<PatientDoctorApply> applies = applyMapper.selectList(wrapper);
        if (applies.isEmpty()) {
            return null;
        }

        PatientDoctorApplyVO vo = applyMapper.selectApplyById(applies.get(0).getId());
        if (vo != null) {
            vo.setApplyTypeText(PatientConstants.applyTypeText(vo.getApplyType()));
            vo.setStatusText(PatientConstants.applyStatusText(vo.getStatus()));
            vo.setRiskLevelText(PatientConstants.riskLevelText(vo.getRiskLevel()));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelApply(Long id) {
        Patient patient = currentPatient();
        PatientDoctorApply apply = applyMapper.selectById(id);
        if (apply == null || !Objects.equals(apply.getPatientId(), patient.getId())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "绑定申请不存在");
        }
        if (!Objects.equals(PatientConstants.APPLY_STATUS_PENDING, apply.getStatus())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该申请已处理，无法取消");
        }

        Long userId = SecurityUtils.getUserId();
        PatientDoctorApply update = new PatientDoctorApply();
        update.setId(id);
        update.setStatus(PatientConstants.APPLY_STATUS_CANCELED);
        update.setUpdateBy(userId);
        update.setUpdateTime(LocalDateTime.now());
        applyMapper.updateById(update);
        log.info("患者取消绑定申请: applyId={}, patientId={}", id, patient.getId());
    }

    /**
     * 当前登录患者档案
     */
    private Patient currentPatient() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
        }
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUserId, userId);
        Patient patient = patientMapper.selectOne(wrapper);
        if (patient == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在");
        }
        return patient;
    }
}
