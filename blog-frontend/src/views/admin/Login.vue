<template>
  <div class="login-page">
    <div class="login-box glass-card">
      <h2>Admin Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Username</label>
          <input v-model="form.username" class="input-field" placeholder="account" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" class="input-field" placeholder="password" required />
        </div>
        <button type="submit" class="btn-primary" style="width: 100%">Login</button>
      </form>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { login } from '../../api/admin'

const router = useRouter()
const authStore = useAuthStore()
const form = reactive({ username: '', password: '' })
const error = ref('')

async function handleLogin() {
  error.value = ''
  console.log('Attempting login with:', form.username)
  try {
    console.log('Sending request to /api/admin/login...')
    const res = await login(form)
    console.log('Login successful, response:', res)
    authStore.setToken(res.token)
    console.log('Navigating to /admin...')
    router.push('/admin')
  } catch (err) {
    console.error('Login failed:', err)
    console.error('Error details:', err.response?.data, err.response?.status)
    error.value = 'Invalid username or password'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 28px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.error {
  color: #f5576c;
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}
</style>
