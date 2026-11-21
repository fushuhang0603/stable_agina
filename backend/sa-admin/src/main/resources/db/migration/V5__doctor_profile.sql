-- =====================================================================
-- V5 医生档案：doctor_profile 独立表，与 sys_user 1:1 关联
-- 说明：姓名 / 职称 / 性别 / 医院 / 科室 / 头像沿用 sys_user，本表不重复存储
-- 可重复执行：CREATE TABLE IF NOT EXISTS + INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

CREATE TABLE IF NOT EXISTS doctor_profile
(
    id                  BIGINT        NOT NULL COMMENT '主键（雪花 ID）',
    user_id             BIGINT        NOT NULL COMMENT '关联 sys_user.id（医生账号）',
    license_no          VARCHAR(64)   DEFAULT NULL COMMENT '执业证书编号',
    license_type        VARCHAR(32)   DEFAULT NULL COMMENT '执业类别：临床 / 中医 / 口腔 / 公共卫生',
    practice_scope      VARCHAR(255)  DEFAULT NULL COMMENT '执业范围',
    cert_no             VARCHAR(64)   DEFAULT NULL COMMENT '资格证书编号',
    issuing_authority   VARCHAR(128)  DEFAULT NULL COMMENT '发证机关',
    license_expire_date DATE          DEFAULT NULL COMMENT '执业证书有效期至',
    position            VARCHAR(64)   DEFAULT NULL COMMENT '行政职务（科主任 / 副主任 / 带组医师）',
    specialty           VARCHAR(255)  DEFAULT NULL COMMENT '擅长领域',
    introduction        VARCHAR(1000) DEFAULT NULL COMMENT '个人简介',
    experience          TEXT          COMMENT '工作与教育经历',
    schedule            VARCHAR(255)  DEFAULT NULL COMMENT '出诊时间',
    registration_fee    DECIMAL(10, 2) DEFAULT NULL COMMENT '挂号费（元）',
    max_patients        INT           DEFAULT NULL COMMENT '在管患者上限',
    create_by           BIGINT        DEFAULT NULL COMMENT '创建人',
    create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           BIGINT        DEFAULT NULL COMMENT '更新人',
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_profile_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='医生档案';

-- ---------------------------------------------------------------------
-- 菜单：医生管理（挂在「系统管理」目录 30 下，仅管理员可见）
-- ---------------------------------------------------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status)
VALUES (112, 30, '医生管理', 'C', '/doctor-management', 'views/DoctorManagement.vue', 'menu:doctor', 'user', 13, 1, 1)
ON DUPLICATE KEY UPDATE parent_id  = VALUES(parent_id),
                        menu_name  = VALUES(menu_name),
                        menu_type  = VALUES(menu_type),
                        path       = VALUES(path),
                        component  = VALUES(component),
                        permission = VALUES(permission),
                        icon       = VALUES(icon),
                        sort       = VALUES(sort),
                        visible    = VALUES(visible),
                        status     = VALUES(status);

-- 超级管理员授权（医生角色不授予医生档案管理权限）
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (1, 112)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
