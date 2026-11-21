import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5176,
    host: '0.0.0.0',
    proxy: {
      '/stableanginaapi': {
        target: 'http://127.0.0.1:9003',
        changeOrigin: true
      }
    }
  }
})
