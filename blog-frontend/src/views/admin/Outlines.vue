<template>
  <div class="outlines-page">
    <div class="page-header">
      <h2>Outlines</h2>
      <button class="btn-primary" @click="openModal()">+ New Outline</button>
    </div>

    <div class="filter-bar">
      <select v-model="selectedCategory" class="input-field" style="width: auto; min-width: 180px" @change="onCategoryChange">
        <option :value="null">All Categories</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>
      <button class="btn-primary" @click="load">Filter</button>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p class="loading-text">Loading...</p>
    </div>

    <div v-else class="list">
      <div v-for="item in pagedOutlines" :key="item.id" class="list-item glass-card">
        <div class="item-info">
          <h4>{{ item.title }}</h4>
          <p>Category: {{ getCategoryName(item.categoryId) }} | Sort: {{ item.sortOrder }}</p>
        </div>
        <div class="item-actions">
          <button class="btn-primary" style="padding: 6px 14px; font-size: 12px" @click="openModal(item)">Edit</button>
          <button class="btn-danger" style="padding: 6px 14px; font-size: 12px" @click="del(item.id)">Delete</button>
        </div>
      </div>
    </div>

    <div class="pagination">
      <span class="page-total">Total: {{ outlines.length }}</span>
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

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal glass-card">
        <h3>{{ editing ? 'Edit Outline' : 'New Outline' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Title</label>
            <input v-model="form.title" class="input-field" required />
          </div>
          <div class="form-group">
            <label>Category</label>
            <select v-model="form.categoryId" class="input-field" required>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Sort Order</label>
            <input v-model.number="form.sortOrder" type="number" class="input-field" />
          </div>
          <div class="form-actions">
            <button type="button" class="btn-primary" style="background: rgba(255,255,255,0.1)" @click="showModal = false">Cancel</button>
            <button type="submit" class="btn-primary">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCategories } from '../../api/category'
import { getOutlines, createOutline, updateOutline, deleteOutline } from '../../api/outline'

const loading = ref(false)
const categories = ref([])
const outlines = ref([])
const showModal = ref(false)
const editing = ref(false)
const form = ref({ title: '', categoryId: null, sortOrder: 0 })
const currentPage = ref(1)
const pageSize = 10
const lastSelectedCategoryId = ref(null)
const jumpPage = ref(1)
const selectedCategory = ref(null)
let editingId = null

const pagedOutlines = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return outlines.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(outlines.value.length / pageSize) || 1)

onMounted(async () => {
  categories.value = await getCategories()
  load()
})

async function load() {
  loading.value = true
  outlines.value = await getOutlines(selectedCategory.value)
  currentPage.value = 1
  jumpPage.value = 1
  loading.value = false
}

function onCategoryChange() {
  currentPage.value = 1
}

function handleJump() {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    currentPage.value = jumpPage.value
  }
}

function getCategoryName(id) {
  const cat = categories.value.find(c => c.id === id)
  return cat ? cat.name : 'Unknown'
}

function openModal(item = null) {
  editing.value = !!item
  editingId = item?.id || null
  form.value = item
    ? { title: item.title, categoryId: item.categoryId, sortOrder: item.sortOrder }
    : { title: '', categoryId: lastSelectedCategoryId.value || categories.value[0]?.id || null, sortOrder: 0 }
  showModal.value = true
}

async function save() {
  if (editing.value) {
    await updateOutline(editingId, form.value)
  } else {
    await createOutline(form.value)
    lastSelectedCategoryId.value = form.value.categoryId
  }
  showModal.value = false
  load()
}

async function del(id) {
  if (!confirm('Delete this outline?')) return
  await deleteOutline(id)
  load()
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

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-info p {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 4px;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: 20px;
}

.modal {
  width: 100%;
  max-width: 420px;
  padding: 32px;
}

.modal h3 {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
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
