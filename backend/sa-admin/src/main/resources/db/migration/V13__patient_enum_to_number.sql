-- =====================================================================
-- V13 患者业务枚举数字化
-- 说明：危险分层、绑定申请类型与状态由字符串改为数字编码，便于排序与统计
--       patient.status（随访状态）保持字符串，不在本次范围内
-- 编码：危险分层 1 低危 / 2 中危 / 3 高危（数值越大风险越高）
--       申请类型 1 首次绑定 / 2 换绑
--       申请状态 1 待审核 / 2 已通过 / 3 已驳回 / 4 已取消
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. patient.risk_level 历史数据转换（未知值按低危兜底）
-- ---------------------------------------------------------------------
UPDATE patient
SET risk_level = CASE risk_level
                     WHEN 'MID' THEN '2'
                     WHEN 'HIGH' THEN '3'
                     ELSE '1'
    END;

ALTER TABLE patient
    MODIFY COLUMN risk_level TINYINT NOT NULL DEFAULT 1 COMMENT '危险分层：1 低危 2 中危 3 高危';

-- ---------------------------------------------------------------------
-- 2. patient_doctor_apply 历史数据转换（未知值按首次绑定、待审核兜底）
-- ---------------------------------------------------------------------
UPDATE patient_doctor_apply
SET apply_type = CASE apply_type
                     WHEN 'CHANGE' THEN '2'
                     ELSE '1'
    END;

UPDATE patient_doctor_apply
SET status = CASE status
                 WHEN 'APPROVED' THEN '2'
                 WHEN 'REJECTED' THEN '3'
                 WHEN 'CANCELED' THEN '4'
                 ELSE '1'
    END;

ALTER TABLE patient_doctor_apply
    MODIFY COLUMN apply_type TINYINT NOT NULL DEFAULT 1 COMMENT '申请类型：1 首次绑定 2 换绑';

ALTER TABLE patient_doctor_apply
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 待审核 2 已通过 3 已驳回 4 已取消';
