<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ open: menuOpen }">
      <div class="sidebar-header">
        <div class="sidebar-header-left">
          <button class="back-btn" @click="goHome" title="Back to Home">←</button>
          <h3>Blog Admin</h3>
        </div>
        <button class="close-menu" @click="menuOpen = false">×</button>
      </div>
      <nav class="sidebar-nav">
        <router-link v-for="item in menu" :key="item.path" :to="item.path" @click="menuOpen = false">
          {{ item.name }}
        </router-link>
        <a href="#" @click.prevent="showPwdForm = true">Change Password</a>
        <a href="#" @click.prevent="logout">Logout</a>
      </nav>
    </aside>

    <div class="main">
      <header class="topbar">
        <button class="menu-toggle" @click="menuOpen = true">☰</button>
        <span class="topbar-title">Admin Panel</span>
      </header>
      <div class="content">
        <router-view />
      </div>
    </div>

    <div v-if="showPwdForm" class="modal-overlay" @click.self="showPwdForm = false">
      <div class="modal-box glass-card">
        <h3>Change Password</h3>
        <form @submit.prevent="handleChangePassword">
          <div class="form-group">
            <label>Old Password</label>
            <input v-model="pwdForm.oldPassword" type="password" class="input-field" required />
          </div>
          <div class="form-group">
            <label>New Password</label>
            <input v-model="pwdForm.newPassword" type="password" class="input-field" required />
          </div>
          <div class="form-group">
            <label>Confirm Password</label>
            <input v-model="pwdForm.confirmPassword" type="password" class="input-field" required />
          </div>
          <p v-if="pwdError" class="error">{{ pwdError }}</p>
          <p v-if="pwdSuccess" class="success">{{ pwdSuccess }}</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="showPwdForm = false">Cancel</button>
            <button type="submit" class="btn-primary">Confirm</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { changePassword } from '../../api/admin'

const router = useRouter()
const authStore = useAuthStore()
const menuOpen = ref(false)
const showPwdForm = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdError = ref('')
const pwdSuccess = ref('')

const menu = [
  { name: 'Dashboard', path: '/admin/dashboard' },
  { name: 'Articles', path: '/admin/articles' },
  { name: 'Outlines', path: '/admin/outlines' },
  { name: 'Categories', path: '/admin/categories' }
]

function goHome() {
  router.push('/')
}

function logout() {
  if (confirm('是否退出登录？')) {
    authStore.logout()
    router.push('/admin/login')
  }
}

async function handleChangePassword() {
  pwdError.value = ''
  pwdSuccess.value = ''
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    pwdError.value = 'New password and confirm password do not match'
    return
  }
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    pwdSuccess.value = 'Password changed successfully'
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    setTimeout(() => {
      showPwdForm.value = false
      pwdSuccess.value = ''
    }, 1500)
  } catch (err) {
    pwdError.value = err.response?.data?.message || 'Failed to change password'
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: rgba(20, 20, 30, 0.9);
  backdrop-filter: blur(12px);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  position: fixed;
  height: 100vh;
  z-index: 100;
  transition: transform 0.3s;
}

.sidebar-header {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-btn {
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.3);
  color: #667eea;
}

.sidebar-header h3 {
  font-size: 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-menu {
  display: none;
  background: none;
  border: none;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
}

.sidebar-nav {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sidebar-nav a {
  padding: 12px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  transition: all 0.3s;
  font-size: 14px;
}

.sidebar-nav a:hover,
.sidebar-nav a.router-link-active {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
}

.main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.topbar {
  display: none;
  padding: 16px 20px;
  background: rgba(20, 20, 30, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  align-items: center;
  gap: 12px;
}

.menu-toggle {
  background: none;
  border: none;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
}

.content {
  padding: 24px;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.modal-box {
  width: 100%;
  max-width: 400px;
  padding: 32px;
}

.modal-box h3 {
  text-align: center;
  margin-bottom: 24px;
  font-size: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.modal-actions button {
  flex: 1;
}

.error {
  color: #f5576c;
  text-align: center;
  font-size: 14px;
  margin-bottom: 12px;
}

.success {
  color: #4ade80;
  text-align: center;
  font-size: 14px;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .close-menu {
    display: block;
  }

  .main {
    margin-left: 0;
  }

  .topbar {
    display: flex;
  }
}
</style>
