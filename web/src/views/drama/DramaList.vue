<template>
  <div class="page-container">
    <div class="content-wrapper">
      <!-- Apple-style Hero Header -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title animate-slide-up">{{ $t('drama.title') }}</h1>
          <p class="hero-subtitle animate-slide-up delay-1">
            {{ $t('drama.totalProjects', { count: total }) }} 个正在创作的精彩故事
          </p>
          <div class="hero-actions animate-slide-up delay-2">
            <el-button type="primary" size="large" @click="handleCreate" class="apple-btn-primary">
              <el-icon><Plus /></el-icon>
              {{ $t('drama.createNew') }}
            </el-button>
          </div>
        </div>
      </section>

      <!-- Project Grid Section -->
      <section class="grid-section">
        <div v-loading="loading" class="projects-grid" :class="{ 'is-empty': !loading && dramas.length === 0 }">
          <!-- Empty state -->
          <EmptyState v-if="!loading && dramas.length === 0" 
            title="开启您的第一部短剧"
            description="在这里，AI 将辅助您完成从创意到分镜的全过程" 
            :icon="Film">
            <el-button type="primary" size="large" @click="handleCreate" class="apple-btn-primary">
              立即开始
            </el-button>
          </EmptyState>

          <!-- Project Cards -->
          <div class="card-wrapper" v-for="drama in dramas" :key="drama.id">
            <ProjectCard :title="drama.title" :description="drama.description"
              :updated-at="drama.updated_at" :episode-count="drama.total_episodes || 0" @click="viewDrama(drama.id)">
              <template #actions>
                <el-button :icon="Edit" circle @click.stop="editDrama(drama.id)" />
                <el-popconfirm :title="$t('drama.deleteConfirm')" @confirm="deleteDrama(drama.id)">
                  <template #reference>
                    <el-button :icon="Delete" circle type="danger" plain @click.stop />
                  </template>
                </el-popconfirm>
              </template>
            </ProjectCard>
          </div>
        </div>
      </section>

      <!-- Edit Dialog -->
      <el-dialog v-model="editDialogVisible" title="编辑剧本项目" width="480px" class="apple-dialog">
        <el-form :model="editForm" label-position="top" class="apple-form">
          <el-form-item label="项目名称">
            <el-input v-model="editForm.title" placeholder="给您的剧本起个响亮的名字" />
          </el-form-item>
          <el-form-item label="故事简介">
            <el-input v-model="editForm.description" type="textarea" :rows="4" placeholder="简述这是一个什么样的故事..." />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="editDialogVisible = false" link>取消</el-button>
            <el-button type="primary" @click="saveEdit" class="apple-btn-primary small">保存更改</el-button>
          </div>
        </template>
      </el-dialog>

      <CreateDramaDialog v-model="createDialogVisible" @created="loadDramas" />
    </div>

    <!-- Floating Pagination -->
    <div v-if="total > 12" class="floating-pagination">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.page_size"
        :total="total" layout="prev, pager, next" @current-change="loadDramas" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Film, Edit, Delete } from '@element-plus/icons-vue'
import { dramaAPI } from '@/api/drama'
import type { Drama, DramaListQuery } from '@/types/drama'
import { ProjectCard, CreateDramaDialog, EmptyState } from '@/components/common'

const router = useRouter()
const loading = ref(false)
const dramas = ref<Drama[]>([])
const total = ref(0)

const queryParams = ref<DramaListQuery>({
  page: 1,
  page_size: 12
})

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({ id: '', title: '', description: '' })

const loadDramas = async () => {
  loading.value = true
  try {
    const res = await dramaAPI.list(queryParams.value)
    dramas.value = res.items || []
    total.value = res.pagination?.total || 0
  } catch (error: any) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => createDialogVisible.value = true
const viewDrama = (id: string) => router.push(`/dramas/${id}`)

const editDrama = async (id: string) => {
  editDialogVisible.value = true
  try {
    const drama = await dramaAPI.get(id)
    editForm.value = { id: drama.id, title: drama.title, description: drama.description || '' }
  } catch (error) { ElMessage.error('获取信息失败') }
}

const saveEdit = async () => {
  if (!editForm.value.title) return ElMessage.warning('请输入名称')
  try {
    await dramaAPI.update(editForm.value.id, { title: editForm.value.title, description: editForm.value.description })
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadDramas()
  } catch (error) { ElMessage.error('保存失败') }
}

const deleteDrama = async (id: string) => {
  try {
    await dramaAPI.delete(id)
    ElMessage.success('删除成功')
    loadDramas()
  } catch (error) { ElMessage.error('删除失败') }
}

onMounted(loadDramas)
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  padding-bottom: 100px;
}

/* Hero Section */
.hero-section {
  padding: 80px 24px 60px;
  text-align: center;
  max-width: 1400px;
  margin: 0 auto;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
}

.hero-subtitle {
  font-size: 24px;
  color: #86868b;
  font-weight: 400;
  margin-bottom: 32px;
}

/* Grid Layout */
.grid-section {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 32px;
  min-height: 400px;
}

.projects-grid.is-empty {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Apple Style Buttons */
.apple-btn-primary {
  background: #007aff;
  border: none;
  border-radius: 999px;
  font-weight: 500;
  padding: 12px 28px;
  height: auto;
  transition: all 0.3s ease;
}

.apple-btn-primary:hover {
  background: #0071e3;
  transform: scale(1.02);
}

.apple-btn-primary.small {
  padding: 8px 20px;
}

/* Floating Pagination */
.floating-pagination {
  position: fixed;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  padding: 8px 24px;
  border-radius: 999px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
  border: 1px solid rgba(0,0,0,0.04);
}

/* Animations */
.animate-slide-up {
  animation: slideUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .hero-subtitle { font-size: 18px; }
  .projects-grid { grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
}

/* Dark mode */
.dark .hero-title { color: #f5f5f7; }
.dark .hero-subtitle { color: #86868b; }
.dark .floating-pagination { background: rgba(29, 29, 31, 0.8); border-color: rgba(255, 255, 255, 0.1); }
</style>
