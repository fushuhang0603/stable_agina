package com.stable_angina.business.constant;

/**
 * 患者业务常量
 */
public final class PatientConstants {

    private PatientConstants() {
    }

    /** 危险分层：低危 */
    public static final int RISK_LOW = 1;
    /** 危险分层：中危 */
    public static final int RISK_MID = 2;
    /** 危险分层：高危 */
    public static final int RISK_HIGH = 3;

    /** 绑定申请类型：首次绑定 */
    public static final int APPLY_TYPE_INIT = 1;
    /** 绑定申请类型：换绑 */
    public static final int APPLY_TYPE_CHANGE = 2;

    /** 绑定申请状态：待审核 */
    public static final int APPLY_STATUS_PENDING = 1;
    /** 绑定申请状态：已通过 */
    public static final int APPLY_STATUS_APPROVED = 2;
    /** 绑定申请状态：已驳回 */
    public static final int APPLY_STATUS_REJECTED = 3;
    /** 绑定申请状态：已取消 */
    public static final int APPLY_STATUS_CANCELED = 4;

    /** 随访状态：随访中 */
    public static final String PATIENT_STATUS_FOLLOWING = "FOLLOWING";
    /** 随访状态：已逾期 */
    public static final String PATIENT_STATUS_OVERDUE = "OVERDUE";

    /** 绑定留痕动作：绑定 */
    public static final String BIND_ACTION_BIND = "BIND";
    /** 绑定留痕动作：解绑 */
    public static final String BIND_ACTION_UNBIND = "UNBIND";
    /** 绑定留痕操作人类型：医生 */
    public static final String OPERATOR_DOCTOR = "DOCTOR";
    /** 绑定留痕操作人类型：管理员 */
    public static final String OPERATOR_ADMIN = "ADMIN";

    /**
     * 危险分层文本
     */
    public static String riskLevelText(Integer riskLevel) {
        if (riskLevel == null) {
            return "";
        }
        return switch (riskLevel) {
            case RISK_HIGH -> "高危";
            case RISK_MID -> "中危";
            case RISK_LOW -> "低危";
            default -> String.valueOf(riskLevel);
        };
    }

    /**
     * 绑定申请类型文本
     */
    public static String applyTypeText(Integer applyType) {
        if (applyType == null) {
            return "";
        }
        return switch (applyType) {
            case APPLY_TYPE_INIT -> "首次绑定";
            case APPLY_TYPE_CHANGE -> "换绑";
            default -> String.valueOf(applyType);
        };
    }

    /**
     * 绑定申请状态文本
     */
    public static String applyStatusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case APPLY_STATUS_PENDING -> "待审核";
            case APPLY_STATUS_APPROVED -> "已通过";
            case APPLY_STATUS_REJECTED -> "已驳回";
            case APPLY_STATUS_CANCELED -> "已取消";
            default -> String.valueOf(status);
        };
    }

    /**
     * 随访状态文本
     */
    public static String statusText(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case PATIENT_STATUS_FOLLOWING -> "随访中";
            case PATIENT_STATUS_OVERDUE -> "已逾期";
            default -> status;
        };
    }
}
