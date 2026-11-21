import { reactive } from 'vue'
import request from '../utils/request'

const TOKEN_KEY = 'sa_token'

const state = reactive({
  token: localStorage.getItem(TOKEN_KEY) || '',
  user: null
})

/** 是否拥有某个权限编码；超级管理员为 "*" 通配 */
function hasPerm(code) {
  if (!code) return true
  const permissions = state.user?.permissions || []
  return permissions.includes('*') || permissions.includes(code)
}

function clearSession() {
  state.token = ''
  state.user = null
  localStorage.removeItem(TOKEN_KEY)
}

/** 账号密码登录：POST /api/auth/login */
async function login({ username, password }) {
  const data = await request.post('/api/auth/login', { username, password })
  state.token = data.token
  state.user = data.userInfoVO
  localStorage.setItem(TOKEN_KEY, data.token)
  return data
}

/** 刷新页面后恢复会话：GET /api/auth/me */
async function fetchMe() {
  if (!state.token) throw new Error('未登录')
  const user = await request.get('/api/auth/me')
  state.user = user
  return user
}

/** 登出：POST /api/auth/logout，令牌服务端立即失效 */
async function logout() {
  try {
    await request.post('/api/auth/logout')
  } finally {
    clearSession()
  }
}

export function useAuthStore() {
  return {
    state,
    login,
    fetchMe,
    logout,
    clearSession,
    hasPerm
  }
}
