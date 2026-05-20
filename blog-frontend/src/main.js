import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/global.css'
import '@wangeditor/editor/dist/css/style.css'
import '@wangeditor/editor'

createApp(App).use(createPinia()).use(router).mount('#app')
