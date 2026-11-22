-- =====================================================================
-- V9 患者档案：患者主索引 / 基线档案 / 档案变更留痕
-- 说明：一患者对应一名管床医生（doctor_id 指向 sys_user.id），暂不建 doctor_patient 多对多表
-- 可重复执行：CREATE TABLE IF NOT EXISTS + INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

CREATE TABLE IF NOT EXISTS patient
(
    id              BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    patient_no      VARCHAR(32)  NOT NULL COMMENT '患者编号',
    user_id         BIGINT       DEFAULT NULL COMMENT '关联 sys_user.id（患者端账号，可空）',
    name            VARCHAR(64)  NOT NULL COMMENT '姓名',
    gender          TINYINT      NOT NULL DEFAULT 0 COMMENT '性别：0 未知 1 男 2 女',
    birth_date      DATE         DEFAULT NULL COMMENT '出生日期',
    phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    diagnose_date   DATE         DEFAULT NULL COMMENT '确诊日期',
    allergy         VARCHAR(255) DEFAULT NULL COMMENT '过敏史',
    comorbidity     VARCHAR(255) DEFAULT NULL COMMENT '合并症',
    risk_level      VARCHAR(16)  NOT NULL DEFAULT 'LOW' COMMENT '危险分层：HIGH 高危 / MID 中危 / LOW 低危',
    status          VARCHAR(16)  NOT NULL DEFAULT 'FOLLOWING' COMMENT '随访状态：FOLLOWING 随访中 / OVERDUE 已逾期',
    doctor_id       BIGINT       DEFAULT NULL COMMENT '管床医生（sys_user.id）',
    last_visit_date DATE         DEFAULT NULL COMMENT '最近随访日期',
    next_visit_date DATE         DEFAULT NULL COMMENT '下次随访日期',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_no (patient_no),
    KEY idx_patient_doctor (doctor_id),
    KEY idx_patient_risk (risk_level),
    KEY idx_patient_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='患者主索引';

CREATE TABLE IF NOT EXISTS patient_baseline
(
    id                  BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    patient_id          BIGINT       NOT NULL COMMENT '关联 patient.id',
    first_attack        TEXT         COMMENT '首次发作描述',
    exam_result         TEXT         COMMENT '检查结果',
    risk_basis          VARCHAR(500) DEFAULT NULL COMMENT '危险分层依据',
    register_doctor_id  BIGINT       DEFAULT NULL COMMENT '建档医生（sys_user.id）',
    register_date       DATE         DEFAULT NULL COMMENT '建档日期',
    create_by           BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_baseline_patient (patient_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='患者基线档案';

CREATE TABLE IF NOT EXISTS patient_change_log
(
    id            BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    patient_id    BIGINT       NOT NULL COMMENT '关联 patient.id',
    field_name    VARCHAR(64)  NOT NULL COMMENT '变更字段名称',
    before_value  VARCHAR(500) DEFAULT NULL COMMENT '变更前值',
    after_value   VARCHAR(500) DEFAULT NULL COMMENT '变更后值',
    operator_name VARCHAR(64)  DEFAULT NULL COMMENT '操作人姓名',
    change_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    PRIMARY KEY (id),
    KEY idx_patient_change_patient (patient_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='患者档案变更留痕';

-- ---------------------------------------------------------------------
-- 演示患者（与前端 mock 口径保持一致，管床医生统一挂到张医生 doctor1）
-- ---------------------------------------------------------------------
INSERT INTO patient (id, patient_no, user_id, name, gender, birth_date, phone, diagnose_date, allergy, comorbidity,
                     risk_level, status, doctor_id, last_visit_date, next_visit_date, remark)
VALUES (1001, 'P2026001', NULL, '张建国', 1, '1964-05-08', '138****6621', '2024-03-12', '磺胺类过敏', '高血压、2 型糖尿病',
        'HIGH', 'FOLLOWING', 2124223590689144833, '2026-11-14', '2026-11-28', '频发发作，血压波动明显'),
       (1002, 'P2026002', NULL, '陈秀兰', 2, '1955-02-14', '139****3082', '2023-08-02', '无', '高血压、慢性肾功能不全',
        'HIGH', 'FOLLOWING', 2124223590689144833, '2026-11-12', '2026-11-26', '依从性较差'),
       (1003, 'P2026003', NULL, '林志强', 1, '1971-09-23', '135****7719', '2025-01-18', '青霉素过敏', '高脂血症',
        'MID', 'FOLLOWING', 2124223590689144833, '2026-11-10', '2026-12-08', NULL),
       (1004, 'P2026004', NULL, '王丽华', 2, '1978-03-30', '136****4408', '2025-06-21', '无', '无',
        'LOW', 'FOLLOWING', 2124223590689144833, '2026-11-08', '2026-12-20', NULL),
       (1005, 'P2026005', NULL, '符海涛', 1, '1959-07-11', '137****2265', '2022-11-09', '阿司匹林不耐受', '高血压、陈旧性心梗',
        'HIGH', 'FOLLOWING', 2124223590689144833, '2026-11-15', '2026-11-22', 'PCI 术后'),
       (1006, 'P2026006', NULL, '黄晓梅', 2, '1967-01-19', '133****9910', '2024-09-30', '无', '2 型糖尿病',
        'MID', 'FOLLOWING', 2124223590689144833, '2026-11-11', '2026-12-05', '血糖偏高'),
       (1007, 'P2026007', NULL, '刘志明', 1, '1952-11-06', '130****5502', '2023-02-14', '造影剂过敏', 'COPD、高血压',
        'HIGH', 'OVERDUE', 2124223590689144833, '2026-10-28', '2026-11-20', '存在失访风险'),
       (1008, 'P2026008', NULL, '何佳怡', 2, '1983-06-27', '188****3345', '2025-10-12', '无', '无',
        'LOW', 'FOLLOWING', 2124223590689144833, '2026-11-05', '2026-12-25', NULL),
       (1009, 'P2026009', NULL, '赵国强', 1, '1957-04-15', '189****1187', '2021-07-08', '无', '高血压',
        'MID', 'FOLLOWING', 2124223590689144833, '2026-11-13', '2026-12-02', '吸烟史'),
       (1010, 'P2026010', NULL, '黎小燕', 2, '1974-08-09', '152****6634', '2025-04-26', '海鲜过敏', '无',
        'LOW', 'FOLLOWING', 2124223590689144833, '2026-11-02', '2026-12-18', NULL),
       (1011, 'P2026011', NULL, '许文斌', 1, '1968-02-21', '186****7701', '2024-12-03', '无', '高脂血症、脂肪肝',
        'MID', 'FOLLOWING', 2124223590689144833, '2026-11-09', '2026-12-11', NULL),
       (1012, 'P2026012', NULL, '罗秀珍', 2, '1961-03-05', '158****2299', '2023-05-17', '无', '高血压、糖尿病、肥胖',
        'HIGH', 'FOLLOWING', 2124223590689144833, '2026-11-16', '2026-11-23', '多重共病')
ON DUPLICATE KEY UPDATE name            = VALUES(name),
                        gender          = VALUES(gender),
                        birth_date      = VALUES(birth_date),
                        phone           = VALUES(phone),
                        diagnose_date   = VALUES(diagnose_date),
                        allergy         = VALUES(allergy),
                        comorbidity     = VALUES(comorbidity),
                        risk_level      = VALUES(risk_level),
                        status          = VALUES(status),
                        doctor_id       = VALUES(doctor_id),
                        last_visit_date = VALUES(last_visit_date),
                        next_visit_date = VALUES(next_visit_date),
                        remark          = VALUES(remark);

-- ---------------------------------------------------------------------
-- 演示患者基线档案
-- ---------------------------------------------------------------------
INSERT INTO patient_baseline (id, patient_id, first_attack, exam_result, risk_basis, register_doctor_id, register_date)
VALUES (2001, 1001,
        '2024-02-28 06:40 晨起快步行走约 300 米后出现胸骨后压榨样疼痛，持续约 8 分钟，休息后缓解。',
        '2024-03-05 冠脉 CTA：LAD 近段混合斑块，管腔狭窄约 65%；RCA 未见明显狭窄。',
        'CCS Ⅲ 级，30 天内发作 9 次，合并高血压与糖尿病', 2124223590689144833, '2024-03-12'),
       (2002, 1002,
        '2023-07-20 情绪激动后出现心前区闷痛，向左肩放射，持续 10 分钟。',
        '2023-07-28 冠脉造影：LAD 中段狭窄 70%，LCX 未见明显狭窄。',
        '合并慢性肾功能不全，血压控制不佳', 2124223590689144833, '2023-08-02'),
       (2003, 1003,
        '2025-01-10 快步上楼时出现胸骨后闷痛，持续约 5 分钟，含服硝酸甘油后缓解。',
        '2025-01-15 运动平板试验阳性，冠脉 CTA：LAD 中段狭窄约 50%。',
        'CCS Ⅱ 级，合并高脂血症', 2124223590689144833, '2025-01-18'),
       (2004, 1004,
        '2025-06-15 劳累后出现短暂胸闷，持续约 3 分钟，休息后自行缓解。',
        '2025-06-19 冠脉 CTA：各支血管未见明显狭窄。',
        'CCS Ⅰ 级，无明确危险因素', 2124223590689144833, '2025-06-21'),
       (2005, 1005,
        '2022-10-30 夜间静息状态下出现心前区压榨样疼痛，持续约 15 分钟。',
        '2022-11-02 冠脉造影：LAD 近段狭窄 80%，行 PCI 植入支架 1 枚。',
        'PCI 术后，合并陈旧性心梗与高血压', 2124223590689144833, '2022-11-09'),
       (2006, 1006,
        '2024-09-24 餐后步行时出现胸闷，持续约 6 分钟，休息后缓解。',
        '2024-09-27 冠脉 CTA：LAD 中段狭窄约 45%，LCX 未见明显狭窄。',
        'CCS Ⅱ 级，合并 2 型糖尿病', 2124223590689144833, '2024-09-30'),
       (2007, 1007,
        '2023-02-08 爬坡时出现胸骨后疼痛，向左臂放射，持续约 12 分钟。',
        '2023-02-11 冠脉造影：LAD 近段狭窄 75%，RCA 中段狭窄 60%。',
        '合并 COPD，30 天内发作频次高', 2124223590689144833, '2023-02-14'),
       (2008, 1008,
        '2025-10-06 剧烈运动后出现短暂胸闷，持续约 2 分钟。',
        '2025-10-09 冠脉 CTA：各支血管未见明显狭窄。',
        'CCS Ⅰ 级，无明确危险因素', 2124223590689144833, '2025-10-12'),
       (2009, 1009,
        '2021-07-01 情绪紧张后出现心前区不适，持续约 5 分钟。',
        '2021-07-05 冠脉 CTA：LAD 中段狭窄约 40%。',
        'CCS Ⅱ 级，长期吸烟', 2124223590689144833, '2021-07-08'),
       (2010, 1010,
        '2025-04-20 快步行走时出现胸闷，持续约 4 分钟，休息后缓解。',
        '2025-04-23 冠脉 CTA：各支血管未见明显狭窄。',
        'CCS Ⅰ 级，过敏体质', 2124223590689144833, '2025-04-26'),
       (2011, 1011,
        '2024-11-27 加班后出现胸骨后闷痛，持续约 7 分钟。',
        '2024-11-30 冠脉 CTA：LAD 中段狭窄约 50%，合并脂肪肝。',
        'CCS Ⅱ 级，合并高脂血症', 2124223590689144833, '2024-12-03'),
       (2012, 1012,
        '2023-05-10 家务劳动后出现心前区疼痛，持续约 10 分钟。',
        '2023-05-13 冠脉造影：LAD 近段狭窄 70%，LCX 中段狭窄 55%。',
        '多重共病，血压与血糖控制欠佳', 2124223590689144833, '2023-05-17')
ON DUPLICATE KEY UPDATE first_attack       = VALUES(first_attack),
                        exam_result        = VALUES(exam_result),
                        risk_basis         = VALUES(risk_basis),
                        register_doctor_id = VALUES(register_doctor_id),
                        register_date      = VALUES(register_date);

-- ---------------------------------------------------------------------
-- 演示档案变更留痕（张建国）
-- ---------------------------------------------------------------------
INSERT INTO patient_change_log (id, patient_id, field_name, before_value, after_value, operator_name, change_time)
VALUES (3001, 1001, '手机号', '138****6600', '138****6621', '张医生', '2026-11-10 09:22:00'),
       (3002, 1001, '合并症', '高血压', '高血压、2 型糖尿病', '张建国', '2026-09-18 15:04:00'),
       (3003, 1001, '过敏史', '无', '磺胺类过敏', '张医生', '2026-06-02 10:41:00')
ON DUPLICATE KEY UPDATE field_name    = VALUES(field_name),
                        before_value  = VALUES(before_value),
                        after_value   = VALUES(after_value),
                        operator_name = VALUES(operator_name),
                        change_time   = VALUES(change_time);
