<template>
  <div class="dashboard">
    <h2>Dashboard</h2>
    <div class="stats">
      <div class="stat-card glass-card">
        <div class="stat-value">{{ stats.categories }}</div>
        <div class="stat-label">Categories</div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-value">{{ stats.outlines }}</div>
        <div class="stat-label">Outlines</div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-value">{{ stats.articles }}</div>
        <div class="stat-label">Articles</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getStats } from '../../api/admin'

const stats = reactive({ categories: 0, outlines: 0, articles: 0 })

onMounted(async () => {
  try {
    const data = await getStats()
    stats.categories = data.categories ?? 0
    stats.outlines = data.outlines ?? 0
    stats.articles = data.articles ?? 0
  } catch (e) {
    console.error('Failed to load stats:', e)
  }
})
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 24px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  padding: 28px;
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.stat-label {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}
</style>
