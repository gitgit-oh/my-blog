import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

console.log('Request module initialized with baseURL:', request.defaults.baseURL)

request.interceptors.request.use(
  (config) => {
    console.log('Request interceptor: Adding auth header')
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    console.log('Request config:', config.method, config.url)
    return config
  },
  (error) => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    console.log('Response received:', response.status, response.config.url)
    return response.data
  },
  (error) => {
    console.error('Response error:', error.message)
    console.error('Response error status:', error.response?.status)
    console.error('Response error data:', error.response?.data)
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/admin/login'
    }
    return Promise.reject(error)
  }
)

export default request
