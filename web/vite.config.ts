import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 3012,
    proxy: {
      '/api': {
        target: 'http://localhost:5679',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1/, '/api')
      },
      '/static': {
        target: 'http://localhost:5679',
        changeOrigin: true
      }
    }
  }
})
