import { ElMessage } from 'element-plus'

/**
 * 全局轻提示：统一走 Element Plus 的 ElMessage。
 * 保留 toast(msg) 的调用签名，避免改动业务页面。
 */
export function toast(msg) {
  ElMessage({ message: msg, type: 'success', duration: 2000, grouping: true })
}
