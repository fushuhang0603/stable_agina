-- =====================================================================
-- V4 菜单目录分组：为角色授权页提供「随访工作台 / 医嘱用药 / 系统管理」三级分组
-- 目录节点 permission 为空，不参与权限判定；仅用于前端树形展示
-- 可重复执行：INSERT ... ON DUPLICATE KEY UPDATE + 幂等 UPDATE
-- =====================================================================

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status)
VALUES (10, 0, '随访工作台', 'M', NULL, NULL, NULL, 'grid', 1, 1, 1),
       (20, 0, '医嘱用药', 'M', NULL, NULL, NULL, 'bag', 2, 1, 1),
       (30, 0, '系统管理', 'M', NULL, NULL, NULL, 'settings', 3, 1, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name),
                        menu_type = VALUES(menu_type),
                        icon      = VALUES(icon),
                        sort      = VALUES(sort),
                        visible   = VALUES(visible),
                        status    = VALUES(status);

-- 随访工作台：统计看板 / 患者管理 / 随访工单 / AI 随访摘要
UPDATE sys_menu SET parent_id = 10 WHERE id IN (100, 101, 102, 103);

-- 医嘱用药：医嘱管理 / 不良反应处置
UPDATE sys_menu SET parent_id = 20 WHERE id IN (104, 105);

-- 系统管理：账号 / 角色 / 参数 / 字典 / 操作日志 / AI 调用日志
UPDATE sys_menu SET parent_id = 30 WHERE id IN (106, 107, 108, 109, 110, 111);

-- 说明：目录节点仅用于前端树形分组，不写入 sys_role_menu，避免与叶子权限计数重复
