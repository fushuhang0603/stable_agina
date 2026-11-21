package com.stable_angina.system.event;

import lombok.Getter;

/**
 * 医生档案初始化事件
 */
@Getter
public class DoctorProfileInitEvent {

    private final Long userId;

    private final Long operatorId;

    public DoctorProfileInitEvent(Long userId, Long operatorId) {
        this.userId = userId;
        this.operatorId = operatorId;
    }
}
