-- =====================================================================
-- V12 医生绑定审核菜单：
--   随访工作台新增「绑定申请审核」（113），超级管理员与医生均可见
--   权限标识与前端 frontend/admin/src/utils/pages.js 的 menu:doctor-apply 对应
-- 可重复执行：INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status)
VALUES (113, 10, '绑定申请审核', 'C', '/doctor-bind-apply', 'views/DoctorBindApply.vue', 'menu:doctor-apply', 'check', 7, 1, 1)
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

-- 超级管理员：绑定申请审核
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (1, 113)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 医生：绑定申请审核
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (2, 113)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
