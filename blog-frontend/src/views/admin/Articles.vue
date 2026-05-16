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
      <button class="btn-primary" @click="loadArticles">Filter</button>
    </div>

    <div class="articles-list">
      <div v-for="article in pagedArticles" :key="article.id" class="article-card glass-card">
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

    <div class="pagination">
      <span class="page-total">Total: {{ articles.length }}</span>
      <button class="btn-secondary" :disabled="currentPage === 1" @click="currentPage = 1">First</button>
      <button class="btn-secondary" :disabled="currentPage === 1" @click="currentPage--">Prev</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="btn-secondary" :disabled="currentPage === totalPages" @click="currentPage++">Next</button>
      <button class="btn-secondary" :disabled="currentPage === totalPages" @click="currentPage = totalPages">Last</button>
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
import { getArticles, deleteArticle } from '../../api/article'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const outlines = ref([])
const articles = ref([])
const selectedCategory = ref(route.query.category ? Number(route.query.category) : null)
const selectedOutline = ref(route.query.outline ? Number(route.query.outline) : null)
const currentPage = ref(1)
const pageSize = 10
const jumpPage = ref(1)

const filteredOutlines = computed(() => {
  if (!selectedCategory.value) return outlines.value
  return outlines.value.filter(o => o.categoryId === selectedCategory.value)
})

const pagedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return articles.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(articles.value.length / pageSize) || 1)

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
  currentPage.value = 1
  jumpPage.value = 1
  if (selectedOutline.value) {
    articles.value = await getArticles(selectedOutline.value)
  } else {
    articles.value = []
    const targetOutlines = filteredOutlines.value.length ? filteredOutlines.value : outlines.value
    for (const o of targetOutlines) {
      const list = await getArticles(o.id)
      articles.value.push(...list)
    }
  }
}

function handleJump() {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    currentPage.value = jumpPage.value
  }
}

async function del(id) {
  if (!confirm('Are you sure to delete this article?')) return
  await deleteArticle(id)
  loadArticles()
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
</style>
