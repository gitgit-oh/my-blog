<template>
  <div class="article-edit">
    <h2>{{ isEdit ? 'Edit Article' : 'New Article' }}</h2>
    <form @submit.prevent="save">
      <div class="form-group">
        <label>Title</label>
        <input v-model="form.title" class="input-field" required />
      </div>
      <div class="form-group">
        <label>Category</label>
        <select v-model="selectedCategory" class="input-field" required @change="onCategoryChange">
          <option :value="null">Select Category</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
      </div>
      <div class="form-group">
        <label>Outline</label>
        <select v-model="form.outlineId" class="input-field" required :disabled="!selectedCategory">
          <option :value="null">Select Outline</option>
          <option v-for="o in filteredOutlines" :key="o.id" :value="o.id">{{ o.title }}</option>
        </select>
      </div>
      <div class="form-group">
        <label>Sort Order</label>
        <input v-model.number="form.sortOrder" type="number" class="input-field" />
      </div>
      <div class="form-group">
        <label>Content</label>
        <div class="editor-wrapper">
          <Editor
            v-model="form.content"
            :defaultConfig="editorConfig"
            mode="default"
            style="height: 400px; background: #fff; color: #333"
          />
        </div>
      </div>
      <div class="form-actions">
        <button type="button" class="btn-primary" style="background: rgba(255,255,255,0.1)" @click="$router.back()">Cancel</button>
        <button type="submit" class="btn-primary">Save</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Editor } from '@wangeditor/editor-for-vue'
import { getCategories } from '../../api/category'
import { getOutlines } from '../../api/outline'
import { getArticle, createArticle, updateArticle } from '../../api/article'
import { uploadImage } from '../../api/admin'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const outlines = ref([])
const selectedCategory = ref(null)
const form = ref({ title: '', outlineId: null, content: '', sortOrder: 0 })

const isEdit = computed(() => !!route.params.id)

const filteredOutlines = computed(() => {
  if (!selectedCategory.value) return []
  return outlines.value.filter(o => o.categoryId === selectedCategory.value)
})

function onCategoryChange() {
  form.value.outlineId = null
}

const editorConfig = {
  placeholder: 'Type here...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        const res = await uploadImage(file)
        insertFn(res.url, file.name, res.url)
      }
    }
  }
}

onMounted(async () => {
  categories.value = await getCategories()
  outlines.value = await getOutlines()
  if (isEdit.value) {
    const article = await getArticle(route.params.id)
    form.value = {
      title: article.title,
      outlineId: article.outlineId,
      content: article.content || '',
      sortOrder: article.sortOrder || 0
    }
    // 根据outline找到对应的category
    const outline = outlines.value.find(o => o.id === article.outlineId)
    if (outline) {
      selectedCategory.value = outline.categoryId
    }
  }
})

async function save() {
  if (isEdit.value) {
    await updateArticle(route.params.id, form.value)
  } else {
    await createArticle(form.value)
  }
  const query = {}
  if (route.query.category) query.category = route.query.category
  if (route.query.outline) query.outline = route.query.outline
  router.push({ path: '/admin/articles', query })
}
</script>

<style scoped>
.article-edit h2 {
  margin-bottom: 24px;
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

.editor-wrapper {
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
