package com.stable_angina.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.DoctorProfileQueryDTO;
import com.stable_angina.business.domain.dto.DoctorProfileUpdateDTO;
import com.stable_angina.business.domain.entity.DoctorProfile;
import com.stable_angina.business.domain.vo.DoctorProfileStatsVO;
import com.stable_angina.business.domain.vo.DoctorProfileVO;
import com.stable_angina.business.mapper.DoctorProfileMapper;
import com.stable_angina.business.service.DoctorService;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.PageResult;
import com.stable_angina.common.constant.CommonConstants;
import com.stable_angina.common.exception.BizException;
import com.stable_angina.system.domain.entity.SysUser;
import com.stable_angina.system.mapper.SysUserMapper;
import com.stable_angina.system.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorProfileMapper doctorProfileMapper;
    @Autowired
    private SysUserMapper userMapper;

    @Override
    public PageResult<DoctorProfileVO> page(DoctorProfileQueryDTO query) {
        long current = query.getPageNum() < 1 ? 1 : query.getPageNum();
        long size = query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), CommonConstants.MAX_PAGE_SIZE);

        Page<SysUser> userPage = doctorProfileMapper.selectDoctorPage(new Page<>(current, size), query);
        List<SysUser> users = userPage.getRecords();

        // 批量补充档案，未建档医生同样出现在列表中
        Map<Long, DoctorProfile> profileMap = new HashMap<>();
        List<Long> userIds = users.stream().map(SysUser::getId).toList();
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<DoctorProfile> profileWrapper = new LambdaQueryWrapper<>();
            profileWrapper.in(DoctorProfile::getUserId, userIds);
            doctorProfileMapper.selectList(profileWrapper)
                    .forEach(profile -> profileMap.put(profile.getUserId(), profile));
        }

        List<DoctorProfileVO> rows = users.stream().map(user -> {
            DoctorProfileVO vo = BeanUtil.copyProperties(user, DoctorProfileVO.class);
            vo.setUserId(user.getId());
            vo.setId(null);
            DoctorProfile profile = profileMap.get(user.getId());
            if (profile != null) {
                BeanUtil.copyProperties(profile, vo);
                vo.setHasProfile(true);
            } else {
                vo.setHasProfile(false);
            }
            vo.setStatusText(user.getStatus() != null && user.getStatus() == CommonConstants.STATUS_ENABLED ? "启用" : "停用");
            return vo;
        }).toList();

        return PageResult.of(rows, userPage.getTotal(), current, size);
    }

    @Override
    public DoctorProfileStatsVO stats() {
        return doctorProfileMapper.selectStats();
    }

    @Override
    public DoctorProfileVO detail(Long userId) {
        DoctorProfileVO vo = doctorProfileMapper.selectDoctorDetail(userId);
        if (vo == null) {
            throw new BizException(ErrorCodes.USER_NOT_FOUND, "医生账号不存在");
        }
        vo.setStatusText(vo.getStatus() != null && vo.getStatus() == CommonConstants.STATUS_ENABLED ? "启用" : "停用");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, DoctorProfileUpdateDTO dto) {
        if (userMapper.selectById(userId) == null) {
            throw new BizException(ErrorCodes.USER_NOT_FOUND, "医生账号不存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, dto.getPhone());
            phoneWrapper.ne(SysUser::getId, userId);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BizException(ErrorCodes.PHONE_EXISTS, "手机号已存在");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Long operatorId = SecurityUtils.getUserId();

        SysUser userUpdate = new SysUser();
        userUpdate.setId(userId);
        userUpdate.setRealName(dto.getRealName().trim());
        userUpdate.setPhone(dto.getPhone());
        userUpdate.setTitle(dto.getTitle());
        userUpdate.setHospital(dto.getHospital());
        userUpdate.setDepartment(dto.getDepartment());
        userUpdate.setUpdateBy(operatorId);
        userUpdate.setUpdateTime(now);
        userMapper.updateById(userUpdate);

        LambdaQueryWrapper<DoctorProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(DoctorProfile::getUserId, userId);
        DoctorProfile existing = doctorProfileMapper.selectOne(profileWrapper);
        DoctorProfile profile = BeanUtil.copyProperties(dto, DoctorProfile.class);
        profile.setUserId(userId);
        profile.setUpdateBy(operatorId);
        profile.setUpdateTime(now);
        if (existing == null) {
            profile.setCreateBy(operatorId);
            profile.setCreateTime(now);
            doctorProfileMapper.insert(profile);
        } else {
            profile.setId(existing.getId());
            doctorProfileMapper.updateById(profile);
        }
        log.info("保存医生档案成功: userId={}", userId);
    }
}
