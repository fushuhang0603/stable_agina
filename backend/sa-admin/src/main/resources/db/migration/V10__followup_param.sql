-- =====================================================================
-- V10 随访参数：followup_param 统一维护血压告警 / 高危判定 / 随访周期 / AI 配置
-- 可重复执行：CREATE TABLE IF NOT EXISTS + INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

CREATE TABLE IF NOT EXISTS followup_param
(
    id          BIGINT       NOT NULL COMMENT '主键（雪花 ID）',
    param_key   VARCHAR(64)  NOT NULL COMMENT '参数键，全局唯一',
    param_name  VARCHAR(64)  NOT NULL COMMENT '参数名称',
    param_group VARCHAR(64)  NOT NULL COMMENT '参数分组',
    param_value VARCHAR(255) NOT NULL DEFAULT '' COMMENT '参数值',
    unit        VARCHAR(32)  DEFAULT NULL COMMENT '单位',
    value_type  VARCHAR(16)  NOT NULL DEFAULT 'TEXT' COMMENT '值类型：NUMBER / TEXT / BOOLEAN',
    description VARCHAR(255) DEFAULT NULL COMMENT '参数说明',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '分组内排序',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 停用',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_followup_param_key (param_key),
    KEY idx_followup_param_group (param_group)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='随访参数';

INSERT INTO followup_param (id, param_key, param_name, param_group, param_value, unit, value_type, description, sort,
                            status)
VALUES (4001, 'bp.sbp.threshold', '收缩压告警阈值', '血压告警', '140', 'mmHg', 'NUMBER', '收缩压 ≥ 该值时标记为偏高', 1, 1),
       (4002, 'bp.dbp.threshold', '舒张压告警阈值', '血压告警', '90', 'mmHg', 'NUMBER', '舒张压 ≥ 该值时标记为偏高', 2, 1),
       (4003, 'risk.attack.count', '高危判定发作次数', '高危判定', '5', '次/7 天', 'NUMBER', '7 天内发作次数达到该值自动标记高危', 1, 1),
       (4004, 'risk.attack.duration', '高危判定持续时长', '高危判定', '10', '分钟', 'NUMBER', '单次发作持续超过该值推送管床医生', 2, 1),
       (4005, 'followup.cycle.high', '高危随访周期', '随访周期', '7', '天', 'NUMBER', '驱动随访工单自动生成', 1, 1),
       (4006, 'followup.cycle.mid', '中危随访周期', '随访周期', '30', '天', 'NUMBER', '驱动随访工单自动生成', 2, 1),
       (4007, 'followup.cycle.low', '低危随访周期', '随访周期', '90', '天', 'NUMBER', '驱动随访工单自动生成', 3, 1),
       (4008, 'ai.base-url', 'AI 接口地址', 'AI 配置', 'https://api.deepseek.com', NULL, 'TEXT', '兼容 OpenAI 协议的 Base URL', 1, 1),
       (4009, 'ai.model', 'AI 模型', 'AI 配置', 'deepseek-chat', NULL, 'TEXT', '摘要生成使用的模型', 2, 1),
       (4010, 'ai.temperature', 'AI temperature', 'AI 配置', '0.2', NULL, 'NUMBER', '越低输出越稳定，摘要场景建议 0.1~0.3', 3, 1),
       (4011, 'ai.max-tokens', 'AI 最大输出长度', 'AI 配置', '1200', 'tokens', 'NUMBER', '单次摘要生成的最大输出长度', 4, 1),
       (4012, 'ai.mock', 'AI Mock 模式', 'AI 配置', 'true', NULL, 'BOOLEAN', '开启后不请求真实大模型，返回内置摘要', 5, 1)
ON DUPLICATE KEY UPDATE param_name  = VALUES(param_name),
                        param_group = VALUES(param_group),
                        param_value = VALUES(param_value),
                        unit        = VALUES(unit),
                        value_type  = VALUES(value_type),
                        description = VALUES(description),
                        sort        = VALUES(sort),
                        status      = VALUES(status);
