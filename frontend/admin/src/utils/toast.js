import { ElMessage } from 'element-plus'

/**
 * 全局轻提示：统一走 Element Plus 的 ElMessage。
 * toast(msg) 用于操作成功等正向提示，toast.error(msg) 用于接口业务异常等失败提示。
 */
function show(msg, type, duration) {
  ElMessage({ message: msg, type, duration, grouping: true })
}

export function toast(msg) {
  show(msg, 'success', 2000)
}

toast.error = function (msg) {
  show(msg, 'error', 2500)
}
