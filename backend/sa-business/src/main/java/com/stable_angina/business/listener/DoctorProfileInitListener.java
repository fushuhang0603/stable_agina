package com.stable_angina.business.listener;

import com.stable_angina.business.domain.entity.DoctorProfile;
import com.stable_angina.business.mapper.DoctorProfileMapper;
import com.stable_angina.common.event.DoctorProfileInitEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 医生档案初始化监听
 */
@Slf4j
@Component
public class DoctorProfileInitListener {

    @Autowired
    private DoctorProfileMapper doctorProfileMapper;

    @EventListener
    public void onDoctorProfileInit(DoctorProfileInitEvent event) {
        LocalDateTime now = LocalDateTime.now();
        DoctorProfile profile = new DoctorProfile();
        profile.setUserId(event.getUserId());
        profile.setCreateBy(event.getOperatorId());
        profile.setCreateTime(now);
        profile.setUpdateBy(event.getOperatorId());
        profile.setUpdateTime(now);
        doctorProfileMapper.insert(profile);
        log.info("医生档案初始化: userId={}", event.getUserId());
    }
}
