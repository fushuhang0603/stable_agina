-- =====================================================================
-- V2 菜单权限种子（V1 只建表未插菜单，登录后菜单权限需要此脚本）
-- 权限标识与前端 frontend/admin/src/utils/pages.js 的 PAGES[].perm 一一对应
-- 可重复执行：INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status)
VALUES (100, 0, '随访统计看板', 'C', '/dashboard', 'views/Dashboard.vue', 'menu:dashboard', 'chart', 1, 1, 1),
       (101, 0, '患者管理', 'C', '/patient-list', 'views/PatientList.vue', 'menu:patient', 'users', 2, 1, 1),
       (102, 0, '随访工单看板', 'C', '/followup-board', 'views/FollowupBoard.vue', 'menu:followup', 'clipboard', 3, 1, 1),
       (103, 0, 'AI 随访摘要', 'C', '/ai-summary', 'views/AiSummary.vue', 'menu:ai-summary', 'activity', 4, 1, 1),
       (104, 0, '医嘱管理', 'C', '/medication-order', 'views/MedicationOrder.vue', 'menu:medication', 'bag', 5, 1, 1),
       (105, 0, '不良反应处置', 'C', '/adverse-reaction', 'views/AdverseReaction.vue', 'menu:adverse', 'alert', 6, 1, 1),
       (106, 0, '账号管理', 'C', '/user-management', 'views/UserManagement.vue', 'menu:user', 'user', 7, 1, 1),
       (107, 0, '角色与权限', 'C', '/role-management', 'views/RoleManagement.vue', 'menu:role', 'shield', 8, 1, 1),
       (108, 0, '系统参数', 'C', '/system-config', 'views/SystemConfig.vue', 'menu:config', 'settings', 9, 1, 1),
       (109, 0, '基础字典', 'C', '/dict-management', 'views/DictManagement.vue', 'menu:dict', 'book', 10, 1, 1),
       (110, 0, '操作日志', 'C', '/operation-log', 'views/OperationLog.vue', 'menu:log', 'list', 11, 1, 1),
       (111, 0, 'AI 调用日志', 'C', '/ai-call-log', 'views/AiCallLog.vue', 'menu:ai-log', 'doc', 12, 1, 1)
ON DUPLICATE KEY UPDATE menu_name   = VALUES(menu_name),
                        menu_type   = VALUES(menu_type),
                        path        = VALUES(path),
                        component   = VALUES(component),
                        permission  = VALUES(permission),
                        icon        = VALUES(icon),
                        sort        = VALUES(sort),
                        visible     = VALUES(visible),
                        status      = VALUES(status);

-- 超级管理员：全部菜单
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
       (1, 106), (1, 107), (1, 108), (1, 109), (1, 110), (1, 111)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 医生：随访工作台 + 医嘱用药
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (2, 100), (2, 101), (2, 102), (2, 103), (2, 104), (2, 105)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
