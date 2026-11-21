import { reactive } from 'vue'

/**
 * 患者端 Mock 数据（前端演示用）
 * 登录患者固定为 P2026001 张建国，字段口径与管理端 mock/index.js 保持一致，
 * 便于后端接口就绪后按同一套字段替换为真实请求。
 */

/** 当前登录患者 */
export const MOCK_PATIENT = {
  id: 'P2026001',
  name: '张建国',
  sex: '男',
  age: 62,
  phone: '138****6621',
  idCard: '3301**********6218',
  risk: '高危',
  doctor: '李医生',
  dept: '附属医院心内科',
  diagnoseDate: '2024-03-12',
  nextVisit: '2026-11-28'
}

/** 基线档案（只读） */
export const BASELINE = {
  firstAttack: '2024-02-28 06:40 晨起快步行走约 300 米后出现胸骨后压榨样疼痛，持续约 8 分钟，休息后缓解。',
  exam: '2024-03-05 冠脉 CTA：LAD 近段混合斑块，管腔狭窄约 65%；RCA 未见明显狭窄。',
  riskLevel: '高危',
  riskBasis: 'CCS Ⅲ 级，30 天内发作 9 次，合并高血压与糖尿病',
  allergy: '磺胺类过敏',
  comorbidity: '高血压、2 型糖尿病',
  registerDoctor: '李医生',
  registerDate: '2024-03-12'
}

/** 档案变更留痕 */
export const ARCHIVE_LOGS = reactive([
  { time: '2026-11-10 09:22', operator: '李医生', field: '联系方式', before: '138****6600', after: '138****6621' },
  { time: '2026-09-18 15:04', operator: '张建国（患者）', field: '合并症', before: '高血压', after: '高血压、2 型糖尿病' },
  { time: '2026-06-02 10:41', operator: '李医生', field: '过敏史', before: '无', after: '磺胺类过敏' }
])

/** 心绞痛发作打卡 */
export const ATTACKS = reactive([
  { id: 'A1001', time: '2026-11-20 06:52', nrs: 7, duration: '8 分钟', trigger: '劳累', relief: '硝酸甘油含服', reliefMin: 3, note: '晨起买菜途中发作', high: true },
  { id: 'A1002', time: '2026-11-18 21:10', nrs: 5, duration: '5 分钟', trigger: '情绪激动', relief: '休息', reliefMin: 6, note: '与家人争执后发作', high: false },
  { id: 'A1003', time: '2026-11-16 07:20', nrs: 6, duration: '6 分钟', trigger: '劳累', relief: '硝酸甘油含服', reliefMin: 4, note: '', high: false },
  { id: 'A1004', time: '2026-11-14 19:45', nrs: 8, duration: '12 分钟', trigger: '饱餐', relief: '硝酸甘油含服', reliefMin: 8, note: '晚餐后发作，含服两次', high: true },
  { id: 'A1005', time: '2026-11-11 06:30', nrs: 4, duration: '4 分钟', trigger: '无明显诱因', relief: '休息', reliefMin: 4, note: '', high: false }
])

/** 血压记录 */
export const BP_RECORDS = reactive([
  { id: 'B1', date: '2026-11-20', period: '晨起', sbp: 152, dbp: 96, hr: 82, level: '偏高' },
  { id: 'B2', date: '2026-11-20', period: '晚间', sbp: 143, dbp: 90, hr: 78, level: '偏高' },
  { id: 'B3', date: '2026-11-19', period: '晨起', sbp: 138, dbp: 86, hr: 76, level: '达标' },
  { id: 'B4', date: '2026-11-19', period: '晚间', sbp: 146, dbp: 92, hr: 80, level: '偏高' },
  { id: 'B5', date: '2026-11-18', period: '晨起', sbp: 135, dbp: 84, hr: 74, level: '达标' },
  { id: 'B6', date: '2026-11-17', period: '晨起', sbp: 140, dbp: 89, hr: 75, level: '达标' },
  { id: 'B7', date: '2026-11-16', period: '晚间', sbp: 144, dbp: 91, hr: 79, level: '偏高' }
])

/** 血压趋势（近 14 天） */
export const BP_TREND = {
  dates: ['11-07', '11-08', '11-09', '11-10', '11-11', '11-12', '11-13', '11-14', '11-15', '11-16', '11-17', '11-18', '11-19', '11-20'],
  sbp: [148, 145, 150, 142, 139, 141, 147, 152, 149, 144, 140, 135, 138, 152],
  dbp: [94, 92, 96, 90, 88, 89, 93, 96, 94, 91, 89, 84, 86, 96]
}

/** 医嘱 */
export const MED_ORDERS = reactive([
  { id: 'M001', drug: '单硝酸异山梨酯缓释片', spec: '40mg×24 片', dosage: '40mg 口服 每日 1 次', period: '晨起', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 78 },
  { id: 'M002', drug: '阿托伐他汀钙片', spec: '20mg×7 片', dosage: '20mg 口服 每晚 1 次', period: '晚间', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 65 },
  { id: 'M003', drug: '琥珀酸美托洛尔缓释片', spec: '47.5mg×7 片', dosage: '47.5mg 口服 每日 1 次', period: '晨起', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 91 },
  { id: 'M004', drug: '阿司匹林肠溶片', spec: '100mg×30 片', dosage: '100mg 口服 每日 1 次', period: '晨起', cycle: '2026-09-01 ~ 2026-12-01', status: '服用中', adherence: 96 }
])

/** 今日服药打卡状态（按医嘱 id 索引，true=已服药） */
export const MED_CHECKS = reactive({
  M001: true,
  M002: false,
  M003: true,
  M004: true
})

/** 不良反应上报记录 */
export const ADVERSE_REACTIONS = reactive([
  {
    id: 'AR001',
    drug: '阿托伐他汀钙片',
    type: '肌肉酸痛',
    desc: '近一周双下肢肌肉酸痛，CK 未查，无尿色加深。',
    reportedAt: '2026-11-19 20:12',
    status: '待处置',
    reply: ''
  }
])

/** 随访工单 */
export const FOLLOWUPS = reactive([
  {
    id: 'F20261120001',
    type: '月度随访',
    risk: '高危',
    dueDate: '2026-11-21',
    status: '待随访',
    doctor: '李医生',
    createdAt: '2026-11-14 00:05'
  }
])

/** 已完成随访结论（患者可查看） */
export const FOLLOWUP_HISTORY = reactive([
  {
    id: 'F20261021011',
    type: '月度随访',
    doctor: '李医生',
    doneAt: '2026-10-21 10:32',
    conclusion: '本周期发作 7 次，较上周期减少，NRS 最高 6 分；血压晨起偏高，建议继续低盐饮食并规律监测；用药依从性可，维持当前方案，按期复诊。'
  }
])

/** 随访问卷（提交后进入医生待确认） */
export const FOLLOWUP_QUESTIONS = [
  { key: 'symptom', title: '近一个月心绞痛发作情况', options: ['未发作', '较前减少', '与之前相当', '较前加重'] },
  { key: 'bp', title: '家庭血压控制情况', options: ['稳定达标', '偶有偏高', '持续偏高', '未规律测量'] },
  { key: 'med', title: '服药依从情况', options: ['按时按量', '偶尔漏服', '经常漏服', '自行调整'] },
  { key: 'visit', title: '近期是否需要门诊复诊', options: ['暂不需要', '需要，希望尽快安排'] }
]

/** 消息中心 */
export const MESSAGES = reactive([
  {
    id: 'MSG1',
    type: '随访待办',
    title: '本月度随访待完成',
    content: '您有一份月度随访问卷待填写，请于 2026-11-21 前完成，逾期将影响随访评估。',
    time: '2026-11-21 08:00',
    read: false,
    link: '/pages/followup/followup'
  },
  {
    id: 'MSG2',
    type: '医生批复',
    title: '李医生已回复您的不良反应上报',
    content: '建议监测肌痛情况，暂不调整他汀剂量，如出现尿色加深请立即就诊。',
    time: '2026-11-20 15:20',
    read: false,
    link: '/pages/medication/medication'
  },
  {
    id: 'MSG3',
    type: '高危提醒',
    title: '检测到高危发作记录',
    content: '您 11-20 06:52 记录的发作 NRS 评分为 7 分，已同步至随访医生，请注意休息并按时服药。',
    time: '2026-11-20 07:10',
    read: false,
    link: '/pages/angina/angina'
  },
  {
    id: 'MSG4',
    type: '血压提醒',
    title: '晨起血压提醒',
    content: '请在起床后 1 小时内、排尿后、服药前测量血压，并完成打卡。',
    time: '2026-11-20 06:00',
    read: true,
    link: '/pages/bp/bp'
  },
  {
    id: 'MSG5',
    type: '用药提醒',
    title: '晚间服药提醒',
    content: '阿托伐他汀钙片 20mg，请于睡前服用。',
    time: '2026-11-19 21:00',
    read: true,
    link: '/pages/medication/medication'
  },
  {
    id: 'MSG6',
    type: '系统公告',
    title: '随访系统升级通知',
    content: '系统将于本周六 22:00-24:00 进行维护，期间打卡功能可能短暂不可用，敬请谅解。',
    time: '2026-11-18 10:00',
    read: true,
    link: ''
  }
])

/** 近 7 天发作次数（用于首页迷你图） */
export const ATTACK_WEEK = [
  { label: '15', count: 0 },
  { label: '16', count: 1 },
  { label: '17', count: 0 },
  { label: '18', count: 1 },
  { label: '19', count: 0 },
  { label: '20', count: 1 },
  { label: '21', count: 0 }
]

/** 今日待办 */
export const TODAY_TASKS = reactive([
  { key: 'med-am', title: '晨起服药', desc: '3 种药物 · 已打卡', done: true, icon: 'pill' },
  { key: 'bp-am', title: '晨起血压测量', desc: '今日尚未记录', done: false, icon: 'heart', url: '/pages/bp/bp' },
  { key: 'med-pm', title: '晚间服药', desc: '阿托伐他汀钙片 20mg', done: false, icon: 'pill', url: '/pages/medication/medication' },
  { key: 'followup', title: '月度随访问卷', desc: '今日 24:00 截止', done: false, icon: 'clipboard', url: '/pages/followup/followup' }
])
