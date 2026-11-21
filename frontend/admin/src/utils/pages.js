/** 页面标题与菜单权限编码（与后端 sys_menu.perm 对应） */
export const PAGES = {
  dashboard: { title: '随访统计看板', perm: 'menu:dashboard' },
  'patient-list': { title: '患者管理', perm: 'menu:patient' },
  'patient-detail': { title: '患者全量档案', perm: 'menu:patient' },
  'followup-board': { title: '随访工单看板', perm: 'menu:followup' },
  'ai-summary': { title: 'AI 随访摘要', perm: 'menu:ai-summary' },
  'medication-order': { title: '医嘱管理', perm: 'menu:medication' },
  'adverse-reaction': { title: '不良反应处置', perm: 'menu:adverse' },
  'user-management': { title: '账号管理', perm: 'menu:user' },
  'doctor-management': { title: '医生管理', perm: 'menu:doctor' },
  'role-management': { title: '角色与权限', perm: 'menu:role' },
  'system-config': { title: '随访参数', perm: 'menu:config' },
  'dict-management': { title: '基础字典', perm: 'menu:dict' },
  'operation-log': { title: '操作日志', perm: 'menu:log' },
  'ai-call-log': { title: 'AI 配置与调用', perm: 'menu:ai-log' }
}

/** 侧栏分组：医生可见「随访工作台 / 医嘱用药」，管理员额外可见「系统管理」 */
export const AD_NAV = [
  {
    group: '随访工作台',
    items: [
      { id: 'dashboard', title: '统计看板', ico: 'chart' },
      { id: 'followup-board', title: '随访工单', ico: 'clipboard' },
      { id: 'ai-summary', title: 'AI 随访摘要', ico: 'activity' },
      { id: 'system-config', title: '随访参数', ico: 'settings' },
      { id: 'patient-list', title: '患者管理', ico: 'users' },
      { id: 'doctor-management', title: '医生管理', ico: 'users' }
    ]
  },
  {
    group: '医嘱用药',
    items: [
      { id: 'medication-order', title: '医嘱管理', ico: 'bag' },
      { id: 'adverse-reaction', title: '不良反应处置', ico: 'alert' }
    ]
  },
  {
    group: '系统管理',
    items: [
      { id: 'user-management', title: '账号管理', ico: 'user' },
      { id: 'role-management', title: '角色与权限', ico: 'shield' },
      { id: 'dict-management', title: '基础字典', ico: 'book' },
      { id: 'operation-log', title: '操作日志', ico: 'list' },
      { id: 'ai-call-log', title: 'AI 配置与调用', ico: 'doc' }
    ]
  }
]
