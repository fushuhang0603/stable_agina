import axios from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'sa_token'

const request = axios.create({
  baseURL: '/stableanginaapi',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number') {
      if (body.code === 0) return body.data
      const error = new Error(body.message || '请求失败')
      error.code = body.code
      throw error
    }
    return body
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      if (!location.pathname.endsWith('/login')) {
        location.href = `${import.meta.env.BASE_URL}login`
      }
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
