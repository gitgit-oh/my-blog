<template>
  <div class="home">
    <header class="header">
      <h1>My Blog</h1>
      <router-link to="/admin" class="admin-link">Sign In</router-link>
    </header>

    <div class="search-bar">
      <input
        v-model="searchKeyword"
        @keyup.enter="handleSearch"
        class="input-field"
        placeholder="Search articles..."
      />
      <button class="btn-primary" @click="handleSearch">Search</button>
    </div>

    <div v-if="searchResults.length" class="search-results glass-card">
      <h3>Search Results</h3>
      <div
        v-for="item in searchResults"
        :key="item.id"
        class="result-item"
        @click="goToArticle(item.id)"
      >
        {{ item.title }}
      </div>
    </div>

    <div class="content">
      <div class="categories">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-section glass-card"
        >
          <h2 class="category-title">{{ cat.name }}</h2>
          <div class="outlines-scroll">
            <div class="outlines">
              <div
                v-for="outline in getOutlinesByCategory(cat.id)"
                :key="outline.id"
                class="outline-item"
                @mouseenter="showArticles(outline.id, $event)"
                @mouseleave="scheduleClose"
              >
                <div class="outline-header">
                  <span class="outline-title">{{ outline.title }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧二级窗口显示articles -->
      <div
        v-if="sidePanelVisible"
        class="side-panel glass-card"
        :style="{ top: sidePanelPosition.top + 'px', left: sidePanelPosition.left + 'px' }"
        @mouseenter="cancelClose"
        @mouseleave="scheduleClose"
      >
        <div class="side-panel-header">
          <h3>{{ sidePanelOutline?.title }}</h3>
        </div>
        <div class="side-panel-articles">
          <div
            v-for="article in sidePanelArticles"
            :key="article.id"
            class="side-article-item"
            @click="goToArticle(article.id)"
          >
            {{ article.title }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { initHome, searchArticles } from '../api/article'

const router = useRouter()
const categories = ref([])
const outlines = ref([])
const articlesMap = ref(new Map())
const searchKeyword = ref('')
const searchResults = ref([])

// 右侧面板状态
const sidePanelVisible = ref(false)
const sidePanelOutline = ref(null)
const sidePanelArticles = ref([])
const sidePanelPosition = ref({ top: 0, left: 0 })
let closeTimer = null

onMounted(async () => {
  // 初始化加载所有数据
  const data = await initHome()
  categories.value = data.categories
  outlines.value = data.outlines
  // 加载所有outline下的articles
  for (const outline of data.outlines) {
    const articles = data.articlesByOutline[outline.id] || []
    articlesMap.value.set(outline.id, articles)
  }
})

function getOutlinesByCategory(categoryId) {
  return outlines.value.filter(o => o.categoryId === categoryId)
}

async function showArticles(outlineId, event) {
  // 清除关闭定时器
  if (closeTimer) {
    clearTimeout(closeTimer)
    closeTimer = null
  }
  
  const outline = outlines.value.find(o => o.id === outlineId)
  if (!outline) return
  
  sidePanelOutline.value = outline
  
  // 从已加载的数据中获取articles
  sidePanelArticles.value = articlesMap.value.get(outlineId) || []
  
  // 计算面板位置：在outline右边
  const targetRect = event.currentTarget.getBoundingClientRect()
  sidePanelPosition.value = {
    top: targetRect.top,
    left: targetRect.right + 10 // outline右边10px
  }
  
  sidePanelVisible.value = true
}

function scheduleClose() {
  // 延迟关闭，给用户时间移动到面板
  closeTimer = setTimeout(() => {
    sidePanelVisible.value = false
    sidePanelOutline.value = null
    sidePanelArticles.value = []
  }, 300)
}

function cancelClose() {
  if (closeTimer) {
    clearTimeout(closeTimer)
    closeTimer = null
  }
}

function goToArticle(id) {
  router.push(`/article/${id}`)
}

async function handleSearch() {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  const res = await searchArticles(searchKeyword.value)
  searchResults.value = res.results || []
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 0;
}

.header h1 {
  font-size: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.admin-link {
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: 14px;
  font-weight: bold;
  transition: color 0.3s;
}

.admin-link:hover {
  color: #667eea;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.search-bar input {
  flex: 1;
}

.search-results {
  padding: 20px;
  margin-bottom: 24px;
}

.search-results h3 {
  margin-bottom: 12px;
  font-size: 16px;
}

.result-item {
  padding: 10px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: color 0.3s;
}

.result-item:hover {
  color: #667eea;
}

.content {
  position: relative;
}

.categories {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.category-section {
  padding: 20px;
  height: 400px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.category-section::-webkit-scrollbar {
  width: 6px;
}

.category-section::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 3px;
}

.category-section::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 3px;
}

.category-section::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

.category-title {
  font-size: 18px;
  margin-bottom: 12px;
  color: #a0a0d0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding-bottom: 8px;
  flex-shrink: 0;
}

.outlines-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.outlines-scroll::-webkit-scrollbar {
  width: 6px;
}

.outlines-scroll::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.outlines-scroll::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 3px;
}

.outlines-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.4);
}

.outlines {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.outline-item {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  overflow: hidden;
}

.outline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  cursor: pointer;
  transition: background 0.3s;
}

.outline-header:hover {
  background: rgba(255, 255, 255, 0.05);
}

.outline-title {
  font-weight: 500;
}

/* 右侧二级面板样式 */
.side-panel {
  position: fixed;
  width: 350px;
  max-height: 500px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  z-index: 1000;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.3);
  animation: slideInRight 0.2s ease;
  pointer-events: auto;
}

@keyframes slideInRight {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.side-panel-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.side-panel-header h3 {
  font-size: 18px;
  color: #a0a0d0;
}

.side-panel-articles {
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 8px;
}

.side-panel-articles::-webkit-scrollbar {
  width: 6px;
}

.side-panel-articles::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 3px;
}

.side-panel-articles::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 3px;
}

.side-panel-articles::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

.side-article-item {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s;
}

.side-article-item:hover {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
}

@media (max-width: 768px) {
  .home {
    padding: 12px;
  }

  .header h1 {
    font-size: 24px;
  }

  .side-panel {
    position: fixed;
    right: 10px;
    left: 10px;
    width: auto;
    top: auto;
    bottom: 20px;
    max-height: 40vh;
  }
}
</style>
