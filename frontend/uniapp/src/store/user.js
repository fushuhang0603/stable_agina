import { defineStore } from 'pinia'
import { request, TOKEN_KEY, PATIENT_KEY } from '@/utils/request'

const MODE_OLD_KEY = 'sa_patient_mode_old'
const TCM_KEY = 'sa_patient_tcm'

/** 手机号中间四位脱敏 */
function maskPhone(phone) {
  const text = String(phone || '')
  return /^1\d{10}$/.test(text) ? text.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2') : text
}

/**
 * 后端 PatientProfileVO（英文口径）转页面使用的扁平结构
 * 页面沿用 mock 时期的字段名，避免各页大改
 */
function buildPatient(profile) {
  if (!profile) return null
  const dept = [profile.hospital, profile.department].filter(Boolean).join(' · ')
  return {
    patientId: profile.id || '',
    id: profile.patientNo || '',
    name: profile.name || '',
    sex: profile.genderText || '',
    age: profile.age || '',
    phone: maskPhone(profile.phone),
    idCard: profile.idCard || '',
    risk: profile.riskLevelText || '',
    doctor: profile.doctorName || '',
    dept,
    diagnoseDate: profile.diagnoseDate || '',
    nextVisit: profile.nextVisitDate || '',
    status: profile.statusText || '',
    doctorId: profile.doctorId || ''
  }
}

/**
 * 患者端登录态
 * 账号体系：用户名 + 密码，注册即建档，医生绑定关系待后续模块补充。
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

    /** 保存登录结果 */
    applyAuth(data) {
      this.token = data.token
      this.patient = buildPatient(data.profile)
      uni.setStorageSync(TOKEN_KEY, this.token)
      uni.setStorageSync(PATIENT_KEY, this.patient)
      return this.patient
    },

    /** 患者注册（注册即建档，成功后直接进入登录态） */
    async register(payload) {
      const data = await request({
        url: '/api/patient/auth/register',
        method: 'POST',
        data: payload,
        auth: false,
        loading: true
      })
      return this.applyAuth(data)
    },

    /** 患者登录 */
    async login({ username, password }) {
      const data = await request({
        url: '/api/patient/auth/login',
        method: 'POST',
        data: { username, password },
        auth: false,
        loading: true
      })
      return this.applyAuth(data)
    },

    /** 拉取本人档案（用于刷新医生绑定、危险分层等医生侧维护的信息） */
    async fetchProfile() {
      const profile = await request({ url: '/api/patient/profile' })
      this.patient = buildPatient(profile)
      uni.setStorageSync(PATIENT_KEY, this.patient)
      return this.patient
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
