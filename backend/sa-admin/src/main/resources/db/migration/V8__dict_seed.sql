-- =====================================================================
-- V8 基础字典种子数据：
--   发作诱因 angina_trigger / 疼痛等级 pain_level
--   药品分类 drug_category / 不良反应类型 adverse_type
-- 说明：仅首次执行，主键固定便于前端联调与后续脚本引用
-- =====================================================================

INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark)
VALUES (1, '发作诱因', 'angina_trigger', 1, '患者发作打卡可选诱因'),
       (2, '疼痛等级', 'pain_level', 1, 'NRS 数字评分法 0~10 分'),
       (3, '药品分类', 'drug_category', 1, '医嘱维护时选择'),
       (4, '不良反应类型', 'adverse_type', 1, '患者上报不良反应时选择');

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, sort, is_default, status)
VALUES (101, 'angina_trigger', '劳累', 'exertion', 1, 0, 1),
       (102, 'angina_trigger', '情绪激动', 'emotion', 2, 0, 1),
       (103, 'angina_trigger', '饱餐', 'meal', 3, 0, 1),
       (104, 'angina_trigger', '受凉', 'cold', 4, 0, 1),
       (105, 'angina_trigger', '无明显诱因', 'none', 5, 0, 1),
       (201, 'pain_level', '0 分 无痛', '0', 1, 0, 1),
       (202, 'pain_level', '1-3 分 轻度', '1-3', 2, 0, 1),
       (203, 'pain_level', '4-6 分 中度', '4-6', 3, 0, 1),
       (204, 'pain_level', '7-10 分 重度', '7-10', 4, 0, 1),
       (301, 'drug_category', '硝酸酯类', 'nitrate', 1, 0, 1),
       (302, 'drug_category', 'β 受体阻滞剂', 'beta', 2, 0, 1),
       (303, 'drug_category', '钙通道阻滞剂', 'ccb', 3, 0, 1),
       (304, 'drug_category', '他汀类', 'statin', 4, 0, 1),
       (305, 'drug_category', '抗血小板药', 'antiplatelet', 5, 0, 1),
       (401, 'adverse_type', '肌肉酸痛', 'myalgia', 1, 0, 1),
       (402, 'adverse_type', '下肢水肿', 'edema', 2, 0, 1),
       (403, 'adverse_type', '皮肤瘀斑', 'bruise', 3, 0, 1),
       (404, 'adverse_type', '头晕乏力', 'dizzy', 4, 0, 1),
       (405, 'adverse_type', '胃肠道不适', 'gi', 5, 0, 1);
