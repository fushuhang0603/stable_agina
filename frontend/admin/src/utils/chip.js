export const CHIP = {
  // 风险分层
  低危: 'chip-green',
  中危: 'chip-amber',
  高危: 'chip-red',
  // 随访工单
  待随访: 'chip-amber',
  随访中: 'chip-blue',
  已完成: 'chip-green',
  已驳回: 'chip-red',
  已逾期: 'chip-red',
  待审核: 'chip-amber',
  已归档: 'chip-green',
  草稿: 'chip-gray',
  // 通用状态
  启用: 'chip-green',
  停用: 'chip-gray',
  在线: 'chip-green',
  离线: 'chip-red',
  正常: 'chip-green',
  异常: 'chip-red',
  // 用药
  服用中: 'chip-blue',
  已停用: 'chip-gray',
  已服药: 'chip-green',
  漏服: 'chip-red',
  // 不良反应
  待处置: 'chip-amber',
  处置中: 'chip-blue',
  已回复: 'chip-green',
  待回复: 'chip-amber',
  // 调用结果
  成功: 'chip-green',
  失败: 'chip-red',
  // AI 人工修改
  已修改: 'chip-amber',
  未修改: 'chip-gray',
  // 血压
  偏高: 'chip-amber',
  偏低: 'chip-blue',
  达标: 'chip-green'
}

export function chipClass(text) {
  return CHIP[text] || 'chip-gray'
}

/** 语义色 -> Element Plus el-tag 的 type */
const TAG_TYPE = {
  'chip-green': 'success',
  'chip-amber': 'warning',
  'chip-red': 'danger',
  'chip-blue': 'primary',
  'chip-violet': 'primary',
  'chip-gray': 'info'
}

export function chipType(text) {
  return TAG_TYPE[chipClass(text)] || 'info'
}
