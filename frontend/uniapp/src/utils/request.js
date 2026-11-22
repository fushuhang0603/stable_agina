/** 患者端统一请求封装 */
import { toast } from '@/utils/format'

const BASE_URL = '/stableanginaapi'
const TIMEOUT = 15000

/** 登录令牌在本地存储中的键名 */
export const TOKEN_KEY = 'sa_patient_token'
/** 患者信息在本地存储中的键名 */
export const PATIENT_KEY = 'sa_patient_info'

function clearLogin() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(PATIENT_KEY)
}

/**
 * 发起请求
 * @param {Object} options
 * @param {string} options.url 接口路径（不含 context-path）
 * @param {string} [options.method] 请求方法，默认 GET
 * @param {Object} [options.data] 请求参数
 * @param {boolean} [options.auth] 是否携带令牌，默认 true
 * @param {boolean} [options.loading] 是否显示加载遮罩，默认 false
 * @returns {Promise<any>} 成功时返回 Result.data
 */
export function request({ url, method = 'GET', data = {}, auth = true, loading = false }) {
  if (loading) {
    uni.showLoading({ title: '加载中', mask: true })
  }
  return new Promise((resolve, reject) => {
    const header = { 'Content-Type': 'application/json' }
    if (auth) {
      const token = uni.getStorageSync(TOKEN_KEY)
      if (token) header.Authorization = `Bearer ${token}`
    }
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      timeout: TIMEOUT,
      success: (res) => {
        const body = res.data || {}
        if (res.statusCode === 200 && body.code === 0) {
          resolve(body.data)
          return
        }
        if (res.statusCode === 401 || body.code === 401) {
          clearLogin()
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error(body.message || '登录已过期，请重新登录'))
          return
        }
        const message = body.message || `请求失败（${res.statusCode}）`
        toast(message)
        reject(new Error(message))
      },
      fail: (err) => {
        toast('网络异常，请稍后重试')
        reject(new Error(err.errMsg || '网络异常'))
      },
      complete: () => {
        if (loading) uni.hideLoading()
      }
    })
  })
}

/** GET 请求 */
export function get(url, data = {}, options = {}) {
  return request({ url, method: 'GET', data, ...options })
}

/** POST 请求 */
export function post(url, data = {}, options = {}) {
  return request({ url, method: 'POST', data, ...options })
}
