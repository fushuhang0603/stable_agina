package com.stable_angina.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.constant.PatientConstants;
import com.stable_angina.business.domain.dto.DoctorApplyHandleDTO;
import com.stable_angina.business.domain.dto.DoctorApplyQueryDTO;
import com.stable_angina.business.domain.entity.DoctorProfile;
import com.stable_angina.business.domain.entity.Patient;
import com.stable_angina.business.domain.entity.PatientChangeLog;
import com.stable_angina.business.domain.entity.PatientDoctorApply;
import com.stable_angina.business.domain.entity.PatientDoctorBindLog;
import com.stable_angina.business.domain.vo.PatientDoctorApplyVO;
import com.stable_angina.business.domain.vo.PatientDoctorOptionVO;
import com.stable_angina.business.mapper.DoctorProfileMapper;
import com.stable_angina.business.mapper.PatientChangeLogMapper;
import com.stable_angina.business.mapper.PatientDoctorApplyMapper;
import com.stable_angina.business.mapper.PatientDoctorBindLogMapper;
import com.stable_angina.business.mapper.PatientDoctorMapper;
import com.stable_angina.business.mapper.PatientMapper;
import com.stable_angina.business.service.DoctorApplyService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DoctorApplyServiceImpl implements DoctorApplyService {

    @Autowired
    private PatientDoctorApplyMapper applyMapper;
    @Autowired
    private PatientDoctorBindLogMapper bindLogMapper;
    @Autowired
    private PatientDoctorMapper patientDoctorMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private PatientChangeLogMapper changeLogMapper;
    @Autowired
    private DoctorProfileMapper doctorProfileMapper;

    @Override
    public PageResult<PatientDoctorApplyVO> page(DoctorApplyQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);

        Page<PatientDoctorApplyVO> voPage = applyMapper.selectApplyPage(new Page<>(current, size), query, currentScopeDoctorId());
        List<PatientDoctorApplyVO> rows = voPage.getRecords();
        for (PatientDoctorApplyVO vo : rows) {
            vo.setApplyTypeText(PatientConstants.applyTypeText(vo.getApplyType()));
            vo.setStatusText(PatientConstants.applyStatusText(vo.getStatus()));
            vo.setRiskLevelText(PatientConstants.riskLevelText(vo.getRiskLevel()));
        }

        PageResult<PatientDoctorApplyVO> result = new PageResult<>();
        result.setRecords(rows);
        result.setTotal(voPage.getTotal());
        result.setPageNum(current);
        result.setPageSize(size);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        PatientDoctorApply apply = applyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "绑定申请不存在");
        }
        if (!Objects.equals(PatientConstants.APPLY_STATUS_PENDING, apply.getStatus())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该申请已处理，请勿重复操作");
        }
        checkDataScope(apply.getDoctorId());

        PatientDoctorOptionVO doctor = patientDoctorMapper.selectDoctorById(apply.getDoctorId());
        if (doctor == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "目标医生不存在或已停用");
        }
        Patient patient = patientMapper.selectById(apply.getPatientId());
        if (patient == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "患者档案不存在");
        }
        if (Objects.equals(patient.getDoctorId(), apply.getDoctorId())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该患者已绑定该医生");
        }

        // 医生在管患者数上限校验
        LambdaQueryWrapper<DoctorProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(DoctorProfile::getUserId, apply.getDoctorId());
        DoctorProfile profile = doctorProfileMapper.selectOne(profileWrapper);
        if (profile != null && profile.getMaxPatients() != null && profile.getMaxPatients() > 0) {
            LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
            patientWrapper.eq(Patient::getDoctorId, apply.getDoctorId());
            long bound = patientMapper.selectCount(patientWrapper);
            if (bound >= profile.getMaxPatients()) {
                throw new BusinessException(ErrorCodes.BAD_REQUEST, "该医生在管患者已达上限，暂时无法绑定");
            }
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long operatorId = SecurityUtils.getUserId();
        String operatorName = loginUser == null ? "系统"
                : (loginUser.getRealName() == null ? loginUser.getUsername() : loginUser.getRealName());
        String operatorType = loginUser != null && loginUser.getRoles() != null
                && loginUser.getRoles().contains(AuthConstants.ROLE_SUPER_ADMIN) ? PatientConstants.OPERATOR_ADMIN : PatientConstants.OPERATOR_DOCTOR;
        LocalDateTime now = LocalDateTime.now();

        Long oldDoctorId = patient.getDoctorId();
        String oldDoctorName = "";
        if (oldDoctorId != null) {
            PatientDoctorOptionVO oldDoctor = patientDoctorMapper.selectDoctorById(oldDoctorId);
            oldDoctorName = oldDoctor == null || oldDoctor.getName() == null ? "" : oldDoctor.getName();
            bindLogMapper.insert(buildBindLog(patient.getId(), oldDoctorId, PatientConstants.BIND_ACTION_UNBIND, operatorType,
                    operatorId, "换绑解绑", now));
        }

        Patient patientUpdate = new Patient();
        patientUpdate.setId(patient.getId());
        patientUpdate.setDoctorId(apply.getDoctorId());
        patientUpdate.setUpdateBy(operatorId);
        patientUpdate.setUpdateTime(now);
        patientMapper.updateById(patientUpdate);

        String newDoctorName = doctor.getName() == null ? "" : doctor.getName();
        bindLogMapper.insert(buildBindLog(patient.getId(), apply.getDoctorId(), PatientConstants.BIND_ACTION_BIND, operatorType,
                operatorId, oldDoctorId == null ? "首次绑定" : "换绑绑定", now));

        PatientChangeLog change = new PatientChangeLog();
        change.setPatientId(patient.getId());
        change.setFieldName("管床医生");
        change.setBeforeValue(oldDoctorName);
        change.setAfterValue(newDoctorName);
        change.setOperatorName(operatorName);
        change.setChangeTime(now);
        changeLogMapper.insert(change);

        PatientDoctorApply applyUpdate = new PatientDoctorApply();
        applyUpdate.setId(id);
        applyUpdate.setStatus(PatientConstants.APPLY_STATUS_APPROVED);
        applyUpdate.setHandleBy(operatorId);
        applyUpdate.setHandleTime(now);
        applyUpdate.setUpdateBy(operatorId);
        applyUpdate.setUpdateTime(now);
        applyMapper.updateById(applyUpdate);

        // 同一患者其他待审核申请自动取消
        LambdaQueryWrapper<PatientDoctorApply> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(PatientDoctorApply::getPatientId, patient.getId());
        pendingWrapper.eq(PatientDoctorApply::getStatus, PatientConstants.APPLY_STATUS_PENDING);
        pendingWrapper.ne(PatientDoctorApply::getId, id);
        List<PatientDoctorApply> others = applyMapper.selectList(pendingWrapper);
        for (PatientDoctorApply other : others) {
            PatientDoctorApply otherUpdate = new PatientDoctorApply();
            otherUpdate.setId(other.getId());
            otherUpdate.setStatus(PatientConstants.APPLY_STATUS_CANCELED);
            otherUpdate.setUpdateBy(operatorId);
            otherUpdate.setUpdateTime(now);
            applyMapper.updateById(otherUpdate);
        }
        log.info("绑定申请审核通过: applyId={}, patientId={}, doctorId={}", id, patient.getId(), apply.getDoctorId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id, DoctorApplyHandleDTO dto) {
        PatientDoctorApply apply = applyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "绑定申请不存在");
        }
        if (!Objects.equals(PatientConstants.APPLY_STATUS_PENDING, apply.getStatus())) {
            throw new BusinessException(ErrorCodes.BAD_REQUEST, "该申请已处理，请勿重复操作");
        }
        checkDataScope(apply.getDoctorId());

        Long operatorId = SecurityUtils.getUserId();
        LocalDateTime now = LocalDateTime.now();
        PatientDoctorApply update = new PatientDoctorApply();
        update.setId(id);
        update.setStatus(PatientConstants.APPLY_STATUS_REJECTED);
        update.setRejectReason(dto.getRejectReason().trim());
        update.setHandleBy(operatorId);
        update.setHandleTime(now);
        update.setUpdateBy(operatorId);
        update.setUpdateTime(now);
        applyMapper.updateById(update);
        log.info("绑定申请已驳回: applyId={}, doctorId={}", id, apply.getDoctorId());
    }

    private PatientDoctorBindLog buildBindLog(Long patientId, Long doctorId, String action, String operatorType,
                                              Long operatorId, String remark, LocalDateTime now) {
        PatientDoctorBindLog bindLog = new PatientDoctorBindLog();
        bindLog.setPatientId(patientId);
        bindLog.setDoctorId(doctorId);
        bindLog.setAction(action);
        bindLog.setOperatorType(operatorType);
        bindLog.setOperatorId(operatorId);
        bindLog.setRemark(remark);
        bindLog.setEffectiveTime(now);
        bindLog.setCreateBy(operatorId);
        bindLog.setCreateTime(now);
        bindLog.setUpdateBy(operatorId);
        bindLog.setUpdateTime(now);
        return bindLog;
    }

    /**
     * 数据范围：超级管理员不限，其他角色仅能处理本人名下的申请
     */
    private void checkDataScope(Long doctorId) {
        Long scopeDoctorId = currentScopeDoctorId();
        if (scopeDoctorId != null && !Objects.equals(scopeDoctorId, doctorId)) {
            throw new BusinessException(ErrorCodes.FORBIDDEN, "无权审核该申请");
        }
    }

    private Long currentScopeDoctorId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getRoles() == null || loginUser.getRoles().contains(AuthConstants.ROLE_SUPER_ADMIN)) {
            return null;
        }
        return loginUser.getUserId();
    }
}
