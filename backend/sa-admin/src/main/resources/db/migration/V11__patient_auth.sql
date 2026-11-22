-- =====================================================================
-- V11 患者端账号：sys_user 补身份证，新增医生绑定申请与绑定留痕
-- 说明：患者注册即建档，sys_user.id_card 作为实名唯一标识；
--       patient.user_id 关联患者端账号，doctor_id 仍为空时表示未绑定医生
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 账号表补身份证字段
-- ---------------------------------------------------------------------
ALTER TABLE sys_user
    ADD COLUMN id_card VARCHAR(32) DEFAULT NULL COMMENT '身份证号' AFTER real_name;

ALTER TABLE sys_user
    ADD UNIQUE KEY uk_sys_user_id_card (id_card);

-- ---------------------------------------------------------------------
-- 2. 患者主索引补账号检索索引
-- ---------------------------------------------------------------------
ALTER TABLE patient
    ADD KEY idx_patient_user (user_id);

-- ---------------------------------------------------------------------
-- 3. 医生绑定申请：患者提交，医生审核后生效
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patient_doctor_apply
(
    id            BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    patient_id    BIGINT       NOT NULL COMMENT '关联 patient.id',
    user_id       BIGINT       NOT NULL COMMENT '申请人 sys_user.id',
    doctor_id     BIGINT       NOT NULL COMMENT '目标医生 sys_user.id',
    apply_type    VARCHAR(16)  NOT NULL DEFAULT 'INIT' COMMENT '申请类型：INIT 首次绑定 / CHANGE 换绑',
    status        VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING 待审核 / APPROVED 已通过 / REJECTED 已驳回 / CANCELED 已取消',
    apply_reason  VARCHAR(500) DEFAULT NULL COMMENT '申请说明',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
    handle_by     BIGINT       DEFAULT NULL COMMENT '处理人 sys_user.id',
    handle_time   DATETIME     DEFAULT NULL COMMENT '处理时间',
    create_by     BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    KEY idx_pda_patient (patient_id),
    KEY idx_pda_doctor (doctor_id),
    KEY idx_pda_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='患者绑定医生申请';

-- ---------------------------------------------------------------------
-- 4. 医生绑定留痕：换绑时保留完整历史
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patient_doctor_bind_log
(
    id             BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    patient_id     BIGINT       NOT NULL COMMENT '关联 patient.id',
    doctor_id      BIGINT       NOT NULL COMMENT '医生 sys_user.id',
    action         VARCHAR(16)  NOT NULL COMMENT '动作：BIND 绑定 / UNBIND 解绑 / CHANGE 换绑',
    operator_type  VARCHAR(16)  NOT NULL COMMENT '操作人类型：PATIENT 患者 / DOCTOR 医生 / ADMIN 管理员',
    operator_id    BIGINT       DEFAULT NULL COMMENT '操作人 sys_user.id',
    remark         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    effective_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    create_by      BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    KEY idx_pdb_patient (patient_id),
    KEY idx_pdb_doctor (doctor_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='患者医生绑定留痕';
