-- =====================================================================
-- V7 随访工作台菜单排序：
--   统计看板 → 随访工单 → AI 随访摘要 → 随访参数 → 患者管理 → 医生管理
-- 可重复执行：幂等 UPDATE
-- =====================================================================

UPDATE sys_menu SET parent_id = 10, sort = 1 WHERE id = 100;

UPDATE sys_menu SET parent_id = 10, sort = 2 WHERE id = 102;

UPDATE sys_menu SET parent_id = 10, sort = 3 WHERE id = 103;

UPDATE sys_menu
SET parent_id = 10,
    sort      = 4,
    menu_name = '随访参数'
WHERE id = 108;

UPDATE sys_menu
SET parent_id = 10,
    sort      = 5,
    menu_name = '患者管理'
WHERE id = 101;

UPDATE sys_menu
SET parent_id = 10,
    sort      = 6,
    menu_name = '医生管理'
WHERE id = 112;
