import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('blog_token') || '')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('blog_token', newToken)
  }

  function logout() {
    token.value = ''
    localStorage.removeItem('blog_token')
  }

  return { token, setToken, logout }
})
