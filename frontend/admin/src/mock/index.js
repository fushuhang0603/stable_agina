import { reactive } from 'vue'

/**
 * 管理端 Mock 数据（前端演示用，后端接口就绪后由 api/* 替换）
 * 数据口径对应需求说明书 5.3 医生端 / 5.4 管理员端
 */

/** 患者主索引 */
export const PATIENTS = reactive([
  {
    id: 'P2026001', name: '张建国', sex: '男', age: 62, phone: '138****6621',
    diagnoseDate: '2024-03-12', risk: '高危', doctor: '李医生', lastVisit: '2026-11-14', nextVisit: '2026-11-28',
    attacks30: 9, bpAbnormal: 12, adherence: 78, status: '随访中',
    allergy: '磺胺类过敏', comorbidity: '高血压、2 型糖尿病',
    tags: ['频发发作', '血压波动']
  },
  {
    id: 'P2026002', name: '陈秀兰', sex: '女', age: 71, phone: '139****3082',
    diagnoseDate: '2023-08-02', risk: '高危', doctor: '李医生', lastVisit: '2026-11-12', nextVisit: '2026-11-26',
    attacks30: 7, bpAbnormal: 16, adherence: 64, status: '随访中',
    allergy: '无', comorbidity: '高血压、慢性肾功能不全',
    tags: ['依从性差', '血压波动']
  },
  {
    id: 'P2026003', name: '林志强', sex: '男', age: 55, phone: '135****7719',
    diagnoseDate: '2025-01-18', risk: '中危', doctor: '李医生', lastVisit: '2026-11-10', nextVisit: '2026-12-08',
    attacks30: 4, bpAbnormal: 5, adherence: 92, status: '随访中',
    allergy: '青霉素过敏', comorbidity: '高脂血症',
    tags: ['血脂异常']
  },
  {
    id: 'P2026004', name: '王丽华', sex: '女', age: 48, phone: '136****4408',
    diagnoseDate: '2025-06-21', risk: '低危', doctor: '李医生', lastVisit: '2026-11-08', nextVisit: '2026-12-20',
    attacks30: 1, bpAbnormal: 0, adherence: 98, status: '随访中',
    allergy: '无', comorbidity: '无',
    tags: []
  },
  {
    id: 'P2026005', name: '符海涛', sex: '男', age: 67, phone: '137****2265',
    diagnoseDate: '2022-11-09', risk: '高危', doctor: '王医生', lastVisit: '2026-11-15', nextVisit: '2026-11-22',
    attacks30: 11, bpAbnormal: 9, adherence: 71, status: '随访中',
    allergy: '阿司匹林不耐受', comorbidity: '高血压、陈旧性心梗',
    tags: ['频发发作', 'PCI 术后']
  },
  {
    id: 'P2026006', name: '黄晓梅', sex: '女', age: 59, phone: '133****9910',
    diagnoseDate: '2024-09-30', risk: '中危', doctor: '王医生', lastVisit: '2026-11-11', nextVisit: '2026-12-05',
    attacks30: 3, bpAbnormal: 3, adherence: 88, status: '随访中',
    allergy: '无', comorbidity: '2 型糖尿病',
    tags: ['血糖偏高']
  },
  {
    id: 'P2026007', name: '刘志明', sex: '男', age: 74, phone: '130****5502',
    diagnoseDate: '2023-02-14', risk: '高危', doctor: '王医生', lastVisit: '2026-10-28', nextVisit: '2026-11-20',
    attacks30: 8, bpAbnormal: 14, adherence: 59, status: '已逾期',
    allergy: '造影剂过敏', comorbidity: 'COPD、高血压',
    tags: ['失访风险', '依从性差']
  },
  {
    id: 'P2026008', name: '何佳怡', sex: '女', age: 43, phone: '188****3345',
    diagnoseDate: '2025-10-12', risk: '低危', doctor: '王医生', lastVisit: '2026-11-05', nextVisit: '2026-12-25',
    attacks30: 0, bpAbnormal: 1, adherence: 100, status: '随访中',
    allergy: '无', comorbidity: '无',
    tags: []
  },
  {
    id: 'P2026009', name: '赵国强', sex: '男', age: 69, phone: '189****1187',
    diagnoseDate: '2021-07-08', risk: '中危', doctor: '李医生', lastVisit: '2026-11-13', nextVisit: '2026-12-02',
    attacks30: 5, bpAbnormal: 6, adherence: 84, status: '随访中',
    allergy: '无', comorbidity: '高血压',
    tags: ['吸烟史']
  },
  {
    id: 'P2026010', name: '黎小燕', sex: '女', age: 52, phone: '152****6634',
    diagnoseDate: '2025-04-26', risk: '低危', doctor: '李医生', lastVisit: '2026-11-02', nextVisit: '2026-12-18',
    attacks30: 1, bpAbnormal: 0, adherence: 96, status: '随访中',
    allergy: '海鲜过敏', comorbidity: '无',
    tags: []
  },
  {
    id: 'P2026011', name: '许文斌', sex: '男', age: 58, phone: '186****7701',
    diagnoseDate: '2024-12-03', risk: '中危', doctor: '王医生', lastVisit: '2026-11-09', nextVisit: '2026-12-11',
    attacks30: 4, bpAbnormal: 4, adherence: 90, status: '随访中',
    allergy: '无', comorbidity: '高脂血症、脂肪肝',
    tags: ['血脂异常']
  },
  {
    id: 'P2026012', name: '罗秀珍', sex: '女', age: 65, phone: '158****2299',
    diagnoseDate: '2023-05-17', risk: '高危', doctor: '王医生', lastVisit: '2026-11-16', nextVisit: '2026-11-23',
    attacks30: 6, bpAbnormal: 10, adherence: 69, status: '随访中',
    allergy: '无', comorbidity: '高血压、糖尿病、肥胖',
    tags: ['多重共病']
  }
])

/** 患者基线档案（只读） */
export const BASELINE = {
  P2026001: {
    firstAttack: '2024-02-28 06:40 晨起快步行走约 300 米后出现胸骨后压榨样疼痛，持续约 8 分钟，休息后缓解。',
    exam: '2024-03-05 冠脉 CTA：LAD 近段混合斑块，管腔狭窄约 65%；RCA 未见明显狭窄。',
    riskLevel: '高危',
    riskBasis: 'CCS Ⅲ 级，30 天内发作 9 次，合并高血压与糖尿病',
    registerDoctor: '李医生',
    registerDate: '2024-03-12'
  },
  P2026002: {
    firstAttack: '2023-07-20 情绪激动后出现心前区闷痛，向左肩放射，持续 10 分钟。',
    exam: '2023-07-28 冠脉造影：LAD 中段狭窄 70%，LCX 未见明显狭窄。',
    riskLevel: '高危',
    riskBasis: '合并慢性肾功能不全，血压控制不佳',
    registerDoctor: '李医生',
    registerDate: '2023-08-02'
  }
}

/** 档案变更留痕 */
export const ARCHIVE_LOGS = reactive([
  { time: '2026-11-10 09:22', operator: '李医生', field: '联系方式', before: '138****6600', after: '138****6621' },
  { time: '2026-09-18 15:04', operator: '张建国（患者）', field: '合并症', before: '高血压', after: '高血压、2 型糖尿病' },
  { time: '2026-06-02 10:41', operator: '李医生', field: '过敏史', before: '无', after: '磺胺类过敏' }
])

/** 心绞痛发作打卡记录 */
export const ATTACKS = reactive([
  { id: 'A1001', patientId: 'P2026001', time: '2026-11-20 06:52', nrs: 7, duration: '8 分钟', trigger: '劳累', relief: '硝酸甘油含服', reliefMin: 3, note: '晨起买菜途中发作', high: true },
  { id: 'A1002', patientId: 'P2026001', time: '2026-11-18 21:10', nrs: 5, duration: '5 分钟', trigger: '情绪激动', relief: '休息', reliefMin: 6, note: '与家人争执后发作', high: false },
  { id: 'A1003', patientId: 'P2026001', time: '2026-11-16 07:20', nrs: 6, duration: '6 分钟', trigger: '劳累', relief: '硝酸甘油含服', reliefMin: 4, note: '', high: false },
  { id: 'A1004', patientId: 'P2026001', time: '2026-11-14 19:45', nrs: 8, duration: '12 分钟', trigger: '饱餐', relief: '硝酸甘油含服', reliefMin: 8, note: '晚餐后发作，含服两次', high: true },
  { id: 'A1005', patientId: 'P2026001', time: '2026-11-11 06:30', nrs: 4, duration: '4 分钟', trigger: '无明显诱因', relief: '休息', reliefMin: 4, note: '', high: false },
  { id: 'A1006', patientId: 'P2026002', time: '2026-11-19 14:30', nrs: 6, duration: '7 分钟', trigger: '受凉', relief: '休息', reliefMin: 9, note: '外出受凉后发作', high: false },
  { id: 'A1007', patientId: 'P2026005', time: '2026-11-20 05:40', nrs: 8, duration: '15 分钟', trigger: '无明显诱因', relief: '硝酸甘油含服', reliefMin: 10, note: '夜间静息发作，已电话联系', high: true }
])

/** 血压记录 */
export const BP_RECORDS = reactive([
  { id: 'B1', patientId: 'P2026001', date: '2026-11-20', period: '晨起', sbp: 152, dbp: 96, hr: 82, level: '偏高' },
  { id: 'B2', patientId: 'P2026001', date: '2026-11-20', period: '晚间', sbp: 143, dbp: 90, hr: 78, level: '偏高' },
  { id: 'B3', patientId: 'P2026001', date: '2026-11-19', period: '晨起', sbp: 138, dbp: 86, hr: 76, level: '达标' },
  { id: 'B4', patientId: 'P2026001', date: '2026-11-19', period: '晚间', sbp: 146, dbp: 92, hr: 80, level: '偏高' },
  { id: 'B5', patientId: 'P2026001', date: '2026-11-18', period: '晨起', sbp: 135, dbp: 84, hr: 74, level: '达标' },
  { id: 'B6', patientId: 'P2026002', date: '2026-11-20', period: '晨起', sbp: 158, dbp: 94, hr: 88, level: '偏高' },
  { id: 'B7', patientId: 'P2026005', date: '2026-11-20', period: '晨起', sbp: 128, dbp: 76, hr: 70, level: '达标' }
])

/** 血压趋势（近 14 天，收缩压/舒张压） */
export const BP_TREND = {
  dates: ['11-07', '11-08', '11-09', '11-10', '11-11', '11-12', '11-13', '11-14', '11-15', '11-16', '11-17', '11-18', '11-19', '11-20'],
  sbp: [148, 145, 150, 142, 139, 141, 147, 152, 149, 144, 140, 135, 138, 152],
  dbp: [94, 92, 96, 90, 88, 89, 93, 96, 94, 91, 89, 84, 86, 96]
}

/** 随访工单 */
export const FOLLOWUPS = reactive([
  { id: 'F20261120001', patientId: 'P2026001', patientName: '张建国', risk: '高危', type: '月度随访', dueDate: '2026-11-21', status: '待随访', doctor: '李医生', createdAt: '2026-11-14 00:05', doneAt: '', conclusion: '' },
  { id: 'F20261120002', patientId: 'P2026007', patientName: '刘志明', risk: '高危', type: '月度随访', dueDate: '2026-11-20', status: '已逾期', doctor: '王医生', createdAt: '2026-10-20 00:05', doneAt: '', conclusion: '' },
  { id: 'F20261120003', patientId: 'P2026005', patientName: '符海涛', risk: '高危', type: '周随访', dueDate: '2026-11-22', status: '待随访', doctor: '王医生', createdAt: '2026-11-15 00:05', doneAt: '', conclusion: '' },
  { id: 'F20261119004', patientId: 'P2026002', patientName: '陈秀兰', risk: '高危', type: '月度随访', dueDate: '2026-11-26', status: '随访中', doctor: '李医生', createdAt: '2026-11-12 00:05', doneAt: '', conclusion: '' },
  { id: 'F20261115005', patientId: 'P2026003', patientName: '林志强', risk: '中危', type: '季度随访', dueDate: '2026-11-15', status: '已完成', doctor: '李医生', createdAt: '2026-08-15 00:05', doneAt: '2026-11-10 10:22', conclusion: '症状稳定，继续当前方案，3 个月后复查。' },
  { id: 'F20261115006', patientId: 'P2026006', patientName: '黄晓梅', risk: '中危', type: '季度随访', dueDate: '2026-11-11', status: '已完成', doctor: '王医生', createdAt: '2026-08-11 00:05', doneAt: '2026-11-11 16:40', conclusion: '血压达标，血糖偏高，建议内分泌科随诊。' },
  { id: 'F20261112007', patientId: 'P2026004', patientName: '王丽华', risk: '低危', type: '半年度随访', dueDate: '2026-11-08', status: '已完成', doctor: '李医生', createdAt: '2026-05-08 00:05', doneAt: '2026-11-08 09:15', conclusion: '无发作，继续保持生活方式干预。' },
  { id: 'F20261110008', patientId: 'P2026009', patientName: '赵国强', risk: '中危', type: '季度随访', dueDate: '2026-11-13', status: '已驳回', doctor: '李医生', createdAt: '2026-08-13 00:05', doneAt: '2026-11-13 11:05', conclusion: 'AI 摘要与原始数据不符，退回重新生成。' },
  { id: 'F20261108009', patientId: 'P2026012', patientName: '罗秀珍', risk: '高危', type: '周随访', dueDate: '2026-11-23', status: '待随访', doctor: '王医生', createdAt: '2026-11-16 00:05', doneAt: '', conclusion: '' },
  { id: 'F20261101010', patientId: 'P2026011', patientName: '许文斌', risk: '中危', type: '季度随访', dueDate: '2026-12-11', status: '待随访', doctor: '王医生', createdAt: '2026-09-11 00:05', doneAt: '', conclusion: '' }
])

/** AI 摘要六段固定结构 */
export const AI_SECTIONS = [
  { key: 'basic', title: '一、基本信息' },
  { key: 'angina', title: '二、心绞痛发作情况' },
  { key: 'bp', title: '三、血压波动' },
  { key: 'adherence', title: '四、用药依从性' },
  { key: 'problems', title: '五、存在问题' },
  { key: 'assessment', title: '六、初步评估' }
]

/** AI 随访摘要记录 */
export const AI_SUMMARIES = reactive([
  {
    id: 'AI20261120001',
    patientId: 'P2026001',
    patientName: '张建国',
    range: '2026-10-21 ~ 2026-11-20',
    createdAt: '2026-11-20 17:32',
    model: 'deepseek-chat',
    temperature: 0.2,
    tokens: 1860,
    duration: '6.4s',
    status: '待审核',
    editor: '',
    editedAt: '',
    sections: {
      basic: '张建国，男，62 岁，2024 年 3 月确诊稳定性心绞痛，合并高血压、2 型糖尿病，基线危险分层为高危，当前由李医生管理。',
      angina: '统计周期内共记录心绞痛发作 9 次，其中 11 月 14 日、11 月 20 日两次 NRS 评分达 8 分与 7 分，持续 8~12 分钟，均需硝酸甘油含服后缓解，已触发高危提醒。',
      bp: '晨起收缩压波动于 135~152 mmHg，舒张压 84~96 mmHg，14 天内有 5 天晨起血压超过 140/90 mmHg 阈值，晚间血压略低于晨起。',
      adherence: '医嘱共 4 种药物，30 天应服 120 次，实际打卡 94 次，依从率 78.3%，漏服集中在晚间他汀类药物。',
      problems: '1. 发作频次较上周期增加 2 次，且出现静息状态下高强度发作；2. 晨起血压控制不达标；3. 晚间服药依从性偏低。',
      assessment: '患者症状控制欠佳，建议 1 周内门诊复评并复查心电图，评估是否需要调整抗心绞痛药物剂量；同时加强晨起血压监测与晚间服药提醒。'
    }
  },
  {
    id: 'AI20261118002',
    patientId: 'P2026002',
    patientName: '陈秀兰',
    range: '2026-10-19 ~ 2026-11-18',
    createdAt: '2026-11-18 10:08',
    model: 'deepseek-chat',
    temperature: 0.2,
    tokens: 1720,
    duration: '5.8s',
    status: '已归档',
    editor: '李医生',
    editedAt: '2026-11-18 10:35',
    sections: {
      basic: '陈秀兰，女，71 岁，2023 年 8 月确诊，合并高血压与慢性肾功能不全，基线危险分层为高危。',
      angina: '统计周期内发作 7 次，NRS 评分 4~6 分，多由受凉与劳累诱发，休息后 9 分钟内可缓解。',
      bp: '血压整体偏高，晨起平均 156/94 mmHg，16 次记录中 12 次超出阈值。',
      adherence: '依从率 64%，漏服次数较多，主要原因为记忆力下降。',
      problems: '血压控制不佳、用药依从性差、存在肾功能不全用药风险。',
      assessment: '建议家属参与服药管理，复评降压方案，并于 1 个月内复查肾功能。'
    }
  },
  {
    id: 'AI20261115003',
    patientId: 'P2026003',
    patientName: '林志强',
    range: '2026-08-16 ~ 2026-11-15',
    createdAt: '2026-11-15 09:20',
    model: 'deepseek-chat',
    temperature: 0.2,
    tokens: 1640,
    duration: '5.1s',
    status: '已归档',
    editor: '李医生',
    editedAt: '2026-11-15 09:41',
    sections: {
      basic: '林志强，男，55 岁，2025 年 1 月确诊，合并高脂血症，基线危险分层为中危。',
      angina: '周期内发作 12 次，均与劳累相关，NRS 评分 3~5 分，休息即可缓解。',
      bp: '血压基本达标，平均 132/82 mmHg。',
      adherence: '依从率 92%，服药规律。',
      problems: 'LDL-C 未达标，运动耐量下降。',
      assessment: '维持现有方案，强化他汀剂量并复查血脂，建议规律有氧运动。'
    }
  }
])

/** 医嘱 */
export const MED_ORDERS = reactive([
  { id: 'M001', patientId: 'P2026001', patientName: '张建国', drug: '单硝酸异山梨酯缓释片', spec: '40mg×24 片', dosage: '40mg 口服 每日 1 次', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 78 },
  { id: 'M002', patientId: 'P2026001', patientName: '张建国', drug: '阿托伐他汀钙片', spec: '20mg×7 片', dosage: '20mg 口服 每晚 1 次', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 65 },
  { id: 'M003', patientId: 'P2026001', patientName: '张建国', drug: '琥珀酸美托洛尔缓释片', spec: '47.5mg×7 片', dosage: '47.5mg 口服 每日 1 次', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 91 },
  { id: 'M004', patientId: 'P2026001', patientName: '张建国', drug: '阿司匹林肠溶片', spec: '100mg×30 片', dosage: '100mg 口服 每日 1 次', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 96 },
  { id: 'M005', patientId: 'P2026002', patientName: '陈秀兰', drug: '苯磺酸氨氯地平片', spec: '5mg×7 片', dosage: '5mg 口服 每日 1 次', cycle: '2026-10-01 ~ 2026-12-31', status: '服用中', adherence: 64 },
  { id: 'M006', patientId: 'P2026005', patientName: '符海涛', drug: '硫酸氢氯吡格雷片', spec: '75mg×7 片', dosage: '75mg 口服 每日 1 次', cycle: '2026-08-15 ~ 2027-02-15', status: '服用中', adherence: 83 },
  { id: 'M007', patientId: 'P2026003', patientName: '林志强', drug: '瑞舒伐他汀钙片', spec: '10mg×7 片', dosage: '10mg 口服 每晚 1 次', cycle: '2026-06-01 ~ 2026-11-01', status: '已停用', adherence: 92 }
])

/** 服药打卡明细（近 7 天） */
export const MED_CHECKS = reactive([
  { id: 'C1', patientId: 'P2026001', patientName: '张建国', drug: '单硝酸异山梨酯缓释片', date: '2026-11-20', period: '晨起', result: '已服药' },
  { id: 'C2', patientId: 'P2026001', patientName: '张建国', drug: '阿托伐他汀钙片', date: '2026-11-20', period: '晚间', result: '漏服' },
  { id: 'C3', patientId: 'P2026001', patientName: '张建国', drug: '阿托伐他汀钙片', date: '2026-11-19', period: '晚间', result: '漏服' },
  { id: 'C4', patientId: 'P2026001', patientName: '张建国', drug: '琥珀酸美托洛尔缓释片', date: '2026-11-19', period: '晨起', result: '已服药' },
  { id: 'C5', patientId: 'P2026002', patientName: '陈秀兰', drug: '苯磺酸氨氯地平片', date: '2026-11-20', period: '晨起', result: '漏服' }
])

/** 不良反应上报 */
export const ADVERSE_REACTIONS = reactive([
  {
    id: 'AR001', patientId: 'P2026001', patientName: '张建国', drug: '阿托伐他汀钙片',
    type: '肌肉酸痛', desc: '近一周双下肢肌肉酸痛，CK 未查，无尿色加深。',
    reportedAt: '2026-11-19 20:12', status: '待处置', reply: '', handledAt: ''
  },
  {
    id: 'AR002', patientId: 'P2026002', patientName: '陈秀兰', drug: '苯磺酸氨氯地平片',
    type: '下肢水肿', desc: '双踝部轻度凹陷性水肿，晨轻暮重。',
    reportedAt: '2026-11-18 09:40', status: '处置中', reply: '建议减量并复查肾功能，2 天后随访。', handledAt: ''
  },
  {
    id: 'AR003', patientId: 'P2026005', patientName: '符海涛', drug: '硫酸氢氯吡格雷片',
    type: '皮肤瘀斑', desc: '前臂出现两处瘀斑，无牙龈出血。',
    reportedAt: '2026-11-15 16:05', status: '已回复', reply: '监测出血倾向，暂不调整剂量，出现黑便立即就诊。', handledAt: '2026-11-16 08:30'
  }
])

/** 后台账号 */
export const ACCOUNTS = reactive([
  { id: 1, username: 'admin', realName: '系统管理员', role: '管理员', org: '中医药人工智能学院', phone: '138****0001', status: '启用', lastLogin: '2026-11-21 08:12' },
  { id: 2, username: 'doctor', realName: '李医生', role: '医生', org: '附属医院心内科', phone: '138****0002', status: '启用', lastLogin: '2026-11-21 07:48' },
  { id: 3, username: 'wangys', realName: '王医生', role: '医生', org: '附属医院心内科', phone: '138****0003', status: '启用', lastLogin: '2026-11-20 18:20' },
  { id: 4, username: 'zhaonurse', realName: '赵护士', role: '医生', org: '附属医院心内科', phone: '138****0004', status: '启用', lastLogin: '2026-11-20 16:02' },
  { id: 5, username: 'auditor', realName: '质控审计', role: '管理员', org: '医务处', phone: '138****0005', status: '启用', lastLogin: '2026-11-19 11:35' },
  { id: 6, username: 'temp01', realName: '轮转医生', role: '医生', org: '附属医院心内科', phone: '138****0006', status: '停用', lastLogin: '2026-09-02 10:11' }
])

/** 角色 */
export const ROLES = reactive([
  { id: 1, code: 'ADMIN', name: '系统管理员', desc: '拥有全部菜单权限与全量数据权限', users: 2, dataScope: '全部数据', perms: 24 },
  { id: 2, code: 'DOCTOR', name: '随访医生', desc: '管理本人负责患者，生成与审核随访摘要', users: 3, dataScope: '本人负责患者', perms: 14 }
])

/** 菜单权限树（角色授权用） */
export const MENU_TREE = [
  {
    key: 'workbench', title: '随访工作台',
    children: [
      { key: 'menu:dashboard', title: '统计看板' },
      { key: 'menu:followup', title: '随访工单' },
      { key: 'menu:ai-summary', title: 'AI 随访摘要' },
      { key: 'menu:config', title: '随访参数' },
      { key: 'menu:patient', title: '患者管理' },
      { key: 'menu:doctor', title: '医生管理' }
    ]
  },
  {
    key: 'medication', title: '医嘱用药',
    children: [
      { key: 'menu:medication', title: '医嘱管理' },
      { key: 'menu:adverse', title: '不良反应处置' }
    ]
  },
  {
    key: 'system', title: '系统管理',
    children: [
      { key: 'menu:user', title: '账号管理' },
      { key: 'menu:role', title: '角色与权限' },
      { key: 'menu:dict', title: '基础字典' },
      { key: 'menu:log', title: '操作日志' },
      { key: 'menu:ai-log', title: 'AI 配置与调用' }
    ]
  }
]

/** 基础字典 */
export const DICTS = reactive([
  {
    code: 'angina_trigger', name: '发作诱因', remark: '患者发作打卡可选诱因',
    items: [
      { label: '劳累', value: 'exertion', sort: 1, status: '启用' },
      { label: '情绪激动', value: 'emotion', sort: 2, status: '启用' },
      { label: '饱餐', value: 'meal', sort: 3, status: '启用' },
      { label: '受凉', value: 'cold', sort: 4, status: '启用' },
      { label: '无明显诱因', value: 'none', sort: 5, status: '启用' }
    ]
  },
  {
    code: 'pain_level', name: '疼痛等级', remark: 'NRS 数字评分法 0~10 分',
    items: [
      { label: '0 分 无痛', value: '0', sort: 1, status: '启用' },
      { label: '1-3 分 轻度', value: '1-3', sort: 2, status: '启用' },
      { label: '4-6 分 中度', value: '4-6', sort: 3, status: '启用' },
      { label: '7-10 分 重度', value: '7-10', sort: 4, status: '启用' }
    ]
  },
  {
    code: 'drug_category', name: '药品分类', remark: '医嘱维护时选择',
    items: [
      { label: '硝酸酯类', value: 'nitrate', sort: 1, status: '启用' },
      { label: 'β 受体阻滞剂', value: 'beta', sort: 2, status: '启用' },
      { label: '钙通道阻滞剂', value: 'ccb', sort: 3, status: '启用' },
      { label: '他汀类', value: 'statin', sort: 4, status: '启用' },
      { label: '抗血小板药', value: 'antiplatelet', sort: 5, status: '启用' }
    ]
  },
  {
    code: 'adverse_type', name: '不良反应类型', remark: '患者上报不良反应时选择',
    items: [
      { label: '肌肉酸痛', value: 'myalgia', sort: 1, status: '启用' },
      { label: '下肢水肿', value: 'edema', sort: 2, status: '启用' },
      { label: '皮肤瘀斑', value: 'bruise', sort: 3, status: '启用' },
      { label: '头晕乏力', value: 'dizzy', sort: 4, status: '启用' },
      { label: '胃肠道不适', value: 'gi', sort: 5, status: '启用' }
    ]
  }
])

/** 随访参数 */
export const CONFIGS = reactive([
  { key: 'bp.sbp.threshold', name: '收缩压告警阈值', value: '140', unit: 'mmHg', group: '血压告警', desc: '收缩压 ≥ 该值时标记为偏高' },
  { key: 'bp.dbp.threshold', name: '舒张压告警阈值', value: '90', unit: 'mmHg', group: '血压告警', desc: '舒张压 ≥ 该值时标记为偏高' },
  { key: 'risk.attack.count', name: '高危判定发作次数', value: '5', unit: '次/7 天', group: '高危判定', desc: '7 天内发作次数达到该值自动标记高危' },
  { key: 'risk.attack.duration', name: '高危判定持续时长', value: '10', unit: '分钟', group: '高危判定', desc: '单次发作持续超过该值推送管床医生' },
  { key: 'followup.cycle.high', name: '高危随访周期', value: '7', unit: '天', group: '随访周期', desc: '驱动随访工单自动生成' },
  { key: 'followup.cycle.mid', name: '中危随访周期', value: '30', unit: '天', group: '随访周期', desc: '驱动随访工单自动生成' },
  { key: 'followup.cycle.low', name: '低危随访周期', value: '90', unit: '天', group: '随访周期', desc: '驱动随访工单自动生成' },
  { key: 'ai.base-url', name: 'AI 接口地址', value: 'https://api.deepseek.com', unit: '', group: 'AI 配置', desc: '兼容 OpenAI 协议的 Base URL' },
  { key: 'ai.model', name: 'AI 模型', value: 'deepseek-chat', unit: '', group: 'AI 配置', desc: '摘要生成使用的模型' },
  { key: 'ai.temperature', name: 'AI temperature', value: '0.2', unit: '', group: 'AI 配置', desc: '越低输出越稳定，摘要场景建议 0.1~0.3' },
  { key: 'ai.max-tokens', name: 'AI 最大输出长度', value: '1200', unit: 'tokens', group: 'AI 配置', desc: '单次摘要生成的最大输出长度' },
  { key: 'ai.mock', name: 'AI Mock 模式', value: 'true', unit: '', group: 'AI 配置', desc: '开启后不请求真实大模型，返回内置摘要' }
])

/** 操作日志 */
export const OPER_LOGS = reactive([
  { id: 1, time: '2026-11-21 09:12:04', user: 'admin', module: '系统参数', action: '修改', target: 'ai.temperature', result: '成功', ip: '192.168.1.24', cost: '18ms' },
  { id: 2, time: '2026-11-21 08:56:31', user: 'doctor', module: 'AI 随访摘要', action: '生成', target: '张建国 / 2026-10-21~2026-11-20', result: '成功', ip: '192.168.1.36', cost: '6.4s' },
  { id: 3, time: '2026-11-21 08:41:02', user: 'doctor', module: '患者管理', action: '查询', target: '风险等级=高危', result: '成功', ip: '192.168.1.36', cost: '42ms' },
  { id: 4, time: '2026-11-20 18:20:11', user: 'wangys', module: '随访工单', action: '归档', target: 'F20261115006', result: '成功', ip: '192.168.1.51', cost: '35ms' },
  { id: 5, time: '2026-11-20 17:32:47', user: 'doctor', module: 'AI 随访摘要', action: '生成', target: '张建国 / 六段摘要', result: '成功', ip: '192.168.1.36', cost: '6.4s' },
  { id: 6, time: '2026-11-20 15:08:22', user: 'admin', module: '账号管理', action: '停用', target: 'temp01', result: '成功', ip: '192.168.1.24', cost: '26ms' },
  { id: 7, time: '2026-11-20 11:44:09', user: 'auditor', module: '操作日志', action: '导出', target: '2026-11 全量日志', result: '成功', ip: '192.168.1.77', cost: '1.2s' },
  { id: 8, time: '2026-11-19 20:15:38', user: 'doctor', module: '不良反应', action: '处置', target: 'AR003', result: '失败', ip: '192.168.1.36', cost: '12ms' }
])

/** AI 调用日志 */
export const AI_LOGS = reactive([
  { id: 1, traceId: 'ai-20261121-001', time: '2026-11-21 08:56:31', user: 'doctor', scene: '随访摘要', patient: '张建国', model: 'deepseek-chat', tokens: 1860, duration: '6.4s', result: '成功', edited: true },
  { id: 2, traceId: 'ai-20261120-014', time: '2026-11-20 17:32:47', user: 'doctor', scene: '随访摘要', patient: '张建国', model: 'deepseek-chat', tokens: 1812, duration: '6.1s', result: '成功', edited: false },
  { id: 3, traceId: 'ai-20261120-009', time: '2026-11-20 10:12:05', user: 'doctor', scene: '随访摘要', patient: '陈秀兰', model: 'deepseek-chat', tokens: 1720, duration: '5.8s', result: '成功', edited: true },
  { id: 4, traceId: 'ai-20261119-021', time: '2026-11-19 16:40:22', user: 'wangys', scene: '随访摘要', patient: '刘志明', model: 'deepseek-chat', tokens: 0, duration: '30.0s', result: '失败', edited: false },
  { id: 5, traceId: 'ai-20261118-006', time: '2026-11-18 10:08:11', user: 'wangys', scene: '随访摘要', patient: '陈秀兰', model: 'deepseek-chat', tokens: 1688, duration: '5.5s', result: '成功', edited: false }
])

/** 看板：近 7 天发作次数 */
export const ATTACK_TREND = {
  dates: ['11-14', '11-15', '11-16', '11-17', '11-18', '11-19', '11-20'],
  counts: [6, 4, 7, 3, 5, 4, 8]
}

/** 看板：随访完成情况 */
export const FOLLOWUP_PROGRESS = { total: 86, done: 71, overdue: 5 }

/** 看板：风险分布 */
export const RISK_DIST = [
  { name: '低危', value: 46, color: '#00B42A' },
  { name: '中危', value: 28, color: '#FF7D00' },
  { name: '高危', value: 12, color: '#F53F3F' }
]

/** 看板：血压异常次数排行 */
export const RISK_RANK = [
  { name: '陈秀兰', value: 16 },
  { name: '刘志明', value: 14 },
  { name: '张建国', value: 12 },
  { name: '罗秀珍', value: 10 },
  { name: '符海涛', value: 9 }
]

/** 工具：按 id 取患者 */
export function getPatient(id) {
  return PATIENTS.find((p) => p.id === id) || PATIENTS[0]
}

/** 工具：当前时间文本 yyyy-MM-dd HH:mm */
export function nowText() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
