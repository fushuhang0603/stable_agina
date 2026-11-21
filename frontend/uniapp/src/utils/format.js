/** 通用格式化与轻提示工具 */
import { useMessageStore } from '@/store/message'

function pad(n) {
  return String(n).padStart(2, '0')
}

/** 2026-11-21 08:30 */
export function nowText(date = new Date()) {
  const d = new Date(date)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 2026-11-21 */
export function todayText(date = new Date()) {
  const d = new Date(date)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

/** 11-21 08:30（列表展示用） */
export function shortTimeText(text) {
  if (!text) return ''
  return String(text).slice(5, 16)
}

/** 问候语 */
export function greetText() {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 11) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
}

/** 血压分级：>=140/90 为偏高 */
export function bpLevel(sbp, dbp) {
  return Number(sbp) >= 140 || Number(dbp) >= 90 ? '偏高' : '达标'
}

/** 心绞痛 NRS 风险：>=7 分触发高危提醒 */
export function nrsLevel(nrs) {
  const n = Number(nrs)
  if (n >= 7) return { text: '高危', cls: 'red' }
  if (n >= 4) return { text: '中度', cls: 'amber' }
  return { text: '轻度', cls: 'green' }
}

/** 数值范围裁剪 */
export function clamp(val, min, max) {
  return Math.min(Math.max(Number(val) || 0, min), max)
}

/** 轻提示：默认走全局 g-message（和仁范式），指定 icon 时回退系统 toast */
export function toast(title, icon = 'none') {
  if (icon !== 'none') {
    uni.showToast({ title, icon, duration: 1800 })
    return
  }
  useMessageStore().showMessage(title, 1800)
}

/** 确认弹窗（Promise 化） */
export function confirm(content, title = '提示') {
  return new Promise((resolve) => {
    uni.showModal({
      title,
      content,
      success: (res) => resolve(res.confirm),
      fail: () => resolve(false)
    })
  })
}
