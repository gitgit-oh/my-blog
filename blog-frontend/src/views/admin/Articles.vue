<template>
  <div class="articles-page">
    <div class="page-header">
      <h2>Articles</h2>
      <button class="btn-primary" @click="goEdit()">+ New Article</button>
    </div>

    <div class="filter-bar">
      <select v-model="selectedCategory" class="input-field" style="width: auto; min-width: 180px" @change="onCategoryChange">
        <option :value="null">All Categories</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>
      <select v-model="selectedOutline" class="input-field" style="width: auto; min-width: 180px">
        <option :value="null">All Outlines</option>
        <option v-for="o in filteredOutlines" :key="o.id" :value="o.id">{{ o.title }}</option>
      </select>
      <button class="btn-primary" @click="filterArticles">Filter</button>
    </div>

    <div class="articles-list" v-if="!loading">
      <div v-for="article in articles" :key="article.id" class="article-card glass-card">
        <div class="article-info">
          <h4>{{ article.title }}</h4>
          <p class="article-date">{{ formatDate(article.createdAt) }} | Sort: {{ article.sortOrder ?? 0 }}</p>
        </div>
        <div class="article-actions">
          <button class="btn-primary" style="padding: 6px 14px; font-size: 12px" @click="goEdit(article.id)">Edit</button>
          <button class="btn-danger" style="padding: 6px 14px; font-size: 12px" @click="del(article.id)">Delete</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p class="loading-text">Loading...</p>
    </div>

    <div class="pagination">
      <span class="page-total">Total: {{ totalArticles }}</span>
      <button class="btn-secondary" :disabled="currentPage === 1" @click="changePage(1)">First</button>
      <button class="btn-secondary" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">Prev</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="btn-secondary" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">Next</button>
      <button class="btn-secondary" :disabled="currentPage === totalPages" @click="changePage(totalPages)">Last</button>
      <div class="page-jump">
        <span>Go to</span>
        <input v-model.number="jumpPage" @keyup.enter="handleJump" class="jump-input" type="number" min="1" :max="totalPages" />
        <button class="btn-secondary" @click="handleJump">Go</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories } from '../../api/category'
import { getOutlines } from '../../api/outline'
import { getArticlesPage, deleteArticle } from '../../api/article'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const outlines = ref([])
const articles = ref([])
const totalArticles = ref(0)
const loading = ref(false)
const selectedCategory = ref(route.query.category ? Number(route.query.category) : null)
const selectedOutline = ref(route.query.outline ? Number(route.query.outline) : null)
const currentPage = ref(1)
const pageSize = 10
const jumpPage = ref(1)

const filteredOutlines = computed(() => {
  if (!selectedCategory.value) return outlines.value
  return outlines.value.filter(o => o.categoryId === selectedCategory.value)
})

const totalPages = computed(() => Math.ceil(totalArticles.value / pageSize) || 1)

onMounted(async () => {
  categories.value = await getCategories()
  outlines.value = await getOutlines()
  loadArticles()
})

function onCategoryChange() {
  selectedOutline.value = null
}

function goEdit(id) {
  const query = {}
  if (selectedCategory.value) query.category = selectedCategory.value
  if (selectedOutline.value) query.outline = selectedOutline.value
  const path = id ? `/admin/articles/edit/${id}` : '/admin/articles/edit'
  router.push({ path, query })
}

async function loadArticles() {
  loading.value = true
  jumpPage.value = currentPage.value
  const res = await getArticlesPage(currentPage.value, pageSize, selectedOutline.value || undefined)
  articles.value = res.content || []
  totalArticles.value = res.totalElements || 0
  loading.value = false
}

async function filterArticles() {
  currentPage.value = 1
  jumpPage.value = 1
  await loadArticles()
}

function changePage(page) {
  if (page >= 1 && page <= totalPages.value && page !== currentPage.value) {
    currentPage.value = page
    loadArticles()
  }
}

function handleJump() {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    changePage(jumpPage.value)
  }
}

async function del(id) {
  if (!confirm('Are you sure to delete this article?')) return
  await deleteArticle(id)
  if (articles.value.length === 1 && currentPage.value > 1) {
    changePage(currentPage.value - 1)
  } else {
    loadArticles()
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-card {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.article-info h4 {
  font-size: 16px;
  margin-bottom: 4px;
}

.article-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.article-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .article-card {
    flex-direction: column;
    align-items: flex-start;
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.page-total {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-right: 8px;
}

.page-info {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.page-jump {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
}

.page-jump span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.jump-input {
  width: 50px;
  padding: 4px 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  font-size: 13px;
  text-align: center;
}

.jump-input:focus {
  outline: none;
  border-color: #667eea;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  gap: 16px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
}
</style>
