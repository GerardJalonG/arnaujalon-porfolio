import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import { resolve } from 'path'

export default defineConfig({
  plugins: [tailwindcss()],
  build: {
    rollupOptions: {
      input: {
        index: resolve(__dirname, 'index.html'),
        about: resolve(__dirname, 'about.html'),
        projects: resolve(__dirname, 'projects.html'),
        qisu: resolve(__dirname, 'qisu.html'),
        qisuprojects: resolve(__dirname, 'qisuprojects.html'),
        sk016: resolve(__dirname, 'sk016.html'),
        outdoor: resolve(__dirname, 'outdoor.html'),
        otros: resolve(__dirname, 'otros.html'),
      },
    },
  },
})
