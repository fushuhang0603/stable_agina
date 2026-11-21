import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 管理端开发服务器：5175；接口统一走 /stableanginaapi 前缀反代到后端 9003
export default defineConfig({
  base: '/admin/',
  plugins: [vue()],
  server: {
    port: 5175,
    open: false,
    proxy: {
      '/stableanginaapi': {
        target: 'http://127.0.0.1:9003',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
})
