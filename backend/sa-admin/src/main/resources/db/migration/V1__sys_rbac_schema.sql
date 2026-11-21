-- =====================================================================
-- V1 系统 / RBAC 基线（对应 docs/01_后端架构设计.md §5.4、§6.2）
-- 约定：CREATE TABLE IF NOT EXISTS + INSERT ... ON DUPLICATE KEY UPDATE（可重复执行）
-- 后续改表一律新增 V{n}__alter_xxx.sql，禁止修改本文件
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 用户
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user
(
    id              BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    username        VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password        VARCHAR(100) NOT NULL COMMENT 'BCrypt 密文',
    real_name       VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
    phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    gender          TINYINT      DEFAULT 0 COMMENT '性别：0 未知 1 男 2 女',
    avatar          VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    title           VARCHAR(64)  DEFAULT NULL COMMENT '职称（医生：主任医师/主治医师…）',
    hospital        VARCHAR(128) DEFAULT NULL COMMENT '所属医院',
    department      VARCHAR(128) DEFAULT NULL COMMENT '所属科室',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 停用',
    last_login_time DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip   VARCHAR(64)  DEFAULT NULL COMMENT '最后登录 IP',
    remark          VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_phone (phone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统用户（管理员 / 医生）';

-- ---------------------------------------------------------------------
-- 2. 角色
-- data_scope：ALL 全部 / ASSIGNED_PATIENTS 本人负责患者 / SELF 仅本人（患者端）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role
(
    id          BIGINT       NOT NULL COMMENT '主键',
    role_name   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(64)  NOT NULL COMMENT '角色标识：SUPER_ADMIN / DOCTOR / PATIENT',
    data_scope  VARCHAR(32)  NOT NULL DEFAULT 'SELF' COMMENT '数据范围：ALL / ASSIGNED_PATIENTS / SELF',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 停用',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_role_key (role_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色';

-- ---------------------------------------------------------------------
-- 3. 用户-角色
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role
(
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    role_id BIGINT NOT NULL COMMENT '角色 ID',
    PRIMARY KEY (user_id, role_id),
    KEY idx_sys_user_role_role (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户与角色关联';

-- ---------------------------------------------------------------------
-- 4. 菜单 / 权限
-- menu_type：M 目录 / C 菜单 / F 按钮；permission 供 @PreAuthorize("hasAuthority(...)")
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_menu
(
    id          BIGINT       NOT NULL COMMENT '主键',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单 ID，0 为根',
    menu_name   VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    menu_type   CHAR(1)      NOT NULL DEFAULT 'C' COMMENT '类型：M 目录 C 菜单 F 按钮',
    path        VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    component   VARCHAR(200) DEFAULT NULL COMMENT '前端组件路径',
    permission  VARCHAR(128) DEFAULT NULL COMMENT '权限标识，如 followup:summary:generate',
    icon        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    visible     TINYINT      NOT NULL DEFAULT 1 COMMENT '是否显示：1 是 0 否',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 停用',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    KEY idx_sys_menu_parent (parent_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='菜单与权限';

-- ---------------------------------------------------------------------
-- 5. 角色-菜单
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role_menu
(
    role_id BIGINT NOT NULL COMMENT '角色 ID',
    menu_id BIGINT NOT NULL COMMENT '菜单 ID',
    PRIMARY KEY (role_id, menu_id),
    KEY idx_sys_role_menu_menu (menu_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色与菜单关联';

-- ---------------------------------------------------------------------
-- 6. 字典类型 / 字典数据
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_dict_type
(
    id          BIGINT       NOT NULL COMMENT '主键',
    dict_name   VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_type   VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 停用',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_dict_type (dict_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='字典类型';

CREATE TABLE IF NOT EXISTS sys_dict_data
(
    id          BIGINT       NOT NULL COMMENT '主键',
    dict_type   VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    dict_label  VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value  VARCHAR(100) NOT NULL COMMENT '字典键值',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    css_class   VARCHAR(64)  DEFAULT NULL COMMENT '样式类',
    is_default  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认：1 是 0 否',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 停用',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    KEY idx_sys_dict_data_type (dict_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='字典数据';

-- ---------------------------------------------------------------------
-- 7. 系统参数（含 AI 开关、模型配置；API Key 加密存储，见需求 §4.4）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_config
(
    id           BIGINT       NOT NULL COMMENT '主键',
    config_name  VARCHAR(100) NOT NULL COMMENT '参数名称',
    config_key   VARCHAR(100) NOT NULL COMMENT '参数键',
    config_value VARCHAR(500) DEFAULT NULL COMMENT '参数值',
    config_type  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否内置：1 是 0 否',
    remark       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_by    BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by    BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_config_key (config_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统参数';

-- ---------------------------------------------------------------------
-- 8. 操作日志（@OperationLog AOP 落库）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_oper_log
(
    id             BIGINT       NOT NULL COMMENT '主键',
    title          VARCHAR(100) DEFAULT NULL COMMENT '模块标题',
    business_type  VARCHAR(32)  DEFAULT NULL COMMENT '业务类型：INSERT/UPDATE/DELETE/EXPORT/OTHER',
    method         VARCHAR(255) DEFAULT NULL COMMENT '方法全限定名',
    request_method VARCHAR(10)  DEFAULT NULL COMMENT 'HTTP 方法',
    oper_name      VARCHAR(64)  DEFAULT NULL COMMENT '操作人账号',
    oper_url       VARCHAR(255) DEFAULT NULL COMMENT '请求 URL',
    oper_ip        VARCHAR(64)  DEFAULT NULL COMMENT '操作 IP',
    oper_param     TEXT         COMMENT '请求参数',
    json_result    TEXT         COMMENT '返回结果',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 成功 0 失败',
    error_msg      TEXT         COMMENT '异常信息',
    cost_time      BIGINT       DEFAULT NULL COMMENT '耗时（毫秒）',
    oper_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_sys_oper_log_time (oper_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='操作日志';

-- ---------------------------------------------------------------------
-- 9. 登录日志（需求：并发登录允许，但需留痕）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_login_log
(
    id         BIGINT       NOT NULL COMMENT '主键',
    username   VARCHAR(64)  DEFAULT NULL COMMENT '登录账号',
    ip         VARCHAR(64)  DEFAULT NULL COMMENT '登录 IP',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '浏览器 UA',
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 成功 0 失败',
    msg        VARCHAR(255) DEFAULT NULL COMMENT '提示信息',
    login_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (id),
    KEY idx_sys_login_log_time (login_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='登录日志';

-- =====================================================================
-- 种子数据（开发环境；BCrypt 密文由 BCryptPasswordEncoder 生成并已验证）
--   admin  / Admin@123   超级管理员，data_scope = ALL
--   doctor / Doctor@123  医生，data_scope = ASSIGNED_PATIENTS
-- 生产环境首次登录后必须修改密码
-- =====================================================================
INSERT INTO sys_role (id, role_name, role_key, data_scope, sort, status, remark)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 'ALL', 1, 1, '系统内置，拥有全部权限'),
       (2, '医生', 'DOCTOR', 'ASSIGNED_PATIENTS', 2, 1, '仅可见本人负责的患者'),
       (3, '患者', 'PATIENT', 'SELF', 3, 1, '仅可见本人数据')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name),
                        data_scope = VALUES(data_scope),
                        status = VALUES(status);

INSERT INTO sys_user (id, username, password, real_name, phone, status, remark)
VALUES (1, 'admin', '$2a$10$u7iQQESHrAK0cCAdXsQ8r.PDfKNILCwnGaytY0uO/d5Oe1C3KIrGW',
        '系统管理员', '13800000000', 1, 'V1 基线种子账号，明文 Admin@123'),
       (2, 'doctor', '$2a$10$Cm0OPxuMbYsP04wnMUQF0O4aIQZ6SHpNNVqGxigTIYhNoTglYK5cC',
        '示例医生', '13900000000', 1, 'V1 基线种子账号，明文 Doctor@123')
ON DUPLICATE KEY UPDATE password = VALUES(password),
                        real_name = VALUES(real_name),
                        status = VALUES(status);

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, remark)
VALUES (1, 'AI 调用开关（Mock 模式）', 'ai.mock', 'true', 1, '暂无 API Key 时返回固定六段样例'),
       (2, '大模型名称', 'ai.model', 'deepseek-chat', 1, 'DeepSeek OpenAI 兼容模型'),
       (3, '随访工单提前生成天数', 'followup.advance.days', '3', 0, '定时任务生成随访工单'),
       (4, '血压高危收缩压阈值', 'monitor.bp.systolic.danger', '180', 0, '超过阈值触发高危提醒'),
       (5, '血压高危舒张压阈值', 'monitor.bp.diastolic.danger', '110', 0, '超过阈值触发高危提醒')
ON DUPLICATE KEY UPDATE config_name = VALUES(config_name),
                        config_value = VALUES(config_value);
