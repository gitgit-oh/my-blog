<template>
  <div class="article-page">
    <div class="article-container glass-card">
      <button class="back-btn" @click="goBack">
        ← Back
      </button>
      <h1 class="article-title">{{ article.title }}</h1>
      <div class="article-meta">
        <span v-if="article.createdAt">{{ formatDate(article.createdAt) }}</span>
        <span v-if="article.updatedAt">Updated: {{ formatDate(article.updatedAt) }}</span>
      </div>
      <div class="article-content" v-html="article.content"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticle } from '../api/article'

const route = useRoute()
const router = useRouter()
const article = ref({})

onMounted(async () => {
  const id = route.params.id
  article.value = await getArticle(id)
})

function goBack() {
  router.push('/')
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.article-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.article-container {
  padding: 40px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #e0e0e0;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.article-title {
  font-size: 28px;
  margin-bottom: 12px;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  gap: 16px;
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #d0d0d0;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin: 24px 0 12px;
  color: #e0e0e0;
}

.article-content :deep(p) {
  margin-bottom: 16px;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 16px 0;
}

.article-content :deep(pre) {
  background: rgba(0, 0, 0, 0.3);
  padding: 16px;
  border-radius: 10px;
  overflow-x: auto;
  margin: 16px 0;
}

.article-content :deep(code) {
  background: rgba(0, 0, 0, 0.3);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 14px;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #667eea;
  padding-left: 16px;
  margin: 16px 0;
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 768px) {
  .article-page {
    padding: 16px;
  }

  .article-container {
    padding: 24px;
  }

  .article-title {
    font-size: 22px;
  }
}
</style>
