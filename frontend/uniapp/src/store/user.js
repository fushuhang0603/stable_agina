import { defineStore } from 'pinia'
import { MOCK_PATIENT } from '@/mock'

const TOKEN_KEY = 'sa_patient_token'
const PATIENT_KEY = 'sa_patient_info'
const MODE_OLD_KEY = 'sa_patient_mode_old'
const TCM_KEY = 'sa_patient_tcm'

/**
 * 患者端登录态（Mock 模式）
 * 任意 11 位手机号 + 6 位验证码（演示码 123456）即可登录，
 * 登录患者固定为 P2026001 张建国，与管理端 mock 数据同源。
 */
export const useUserStore = defineStore('patientUser', {
  state: () => ({
    token: '',
    patient: null,
    modeOld: false,
    tcmStyle: false
  }),

  getters: {
    isLogin: (state) => !!state.token && !!state.patient,
    /** 根节点模式类：适老 / 中医风 */
    pageClass: (state) => {
      const list = []
      if (state.modeOld) list.push('system-mode-old')
      if (state.tcmStyle) list.push('system-style-medical')
      return list.join(' ')
    }
  },

  actions: {
    /** 应用启动时恢复本地登录态 */
    restore() {
      const token = uni.getStorageSync(TOKEN_KEY)
      const patient = uni.getStorageSync(PATIENT_KEY)
      this.token = token || ''
      this.patient = patient || null
      this.modeOld = !!uni.getStorageSync(MODE_OLD_KEY)
      this.tcmStyle = !!uni.getStorageSync(TCM_KEY)
    },

    /** Mock 登录：不发请求，本地校验后写入 storage */
    login({ phone, code }) {
      const phoneText = String(phone || '').trim()
      if (!/^1\d{10}$/.test(phoneText)) {
        throw new Error('请输入 11 位手机号')
      }
      if (String(code || '').trim().length !== 6) {
        throw new Error('请输入 6 位验证码')
      }
      const token = `mock.${phoneText}.${Date.now()}`
      const patient = { ...MOCK_PATIENT, phone: phoneText.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2') }
      this.token = token
      this.patient = patient
      uni.setStorageSync(TOKEN_KEY, token)
      uni.setStorageSync(PATIENT_KEY, patient)
      return patient
    },

    logout() {
      this.token = ''
      this.patient = null
      uni.removeStorageSync(TOKEN_KEY)
      uni.removeStorageSync(PATIENT_KEY)
    },

    /** 适老模式（仅覆盖字号与控件尺寸变量） */
    setModeOld(val) {
      this.modeOld = !!val
      uni.setStorageSync(MODE_OLD_KEY, this.modeOld)
    },

    /** 中医风皮肤（品牌色转暖棕） */
    setTcmStyle(val) {
      this.tcmStyle = !!val
      uni.setStorageSync(TCM_KEY, this.tcmStyle)
    },

    /** 未登录时跳转登录页 */
    requireLogin() {
      if (this.isLogin) return true
      uni.reLaunch({ url: '/pages/login/login' })
      return false
    }
  }
})
