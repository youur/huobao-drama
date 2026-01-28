<template>
  <article 
    class="project-card"
    @click="$emit('click')"
    tabindex="0"
    @keydown.enter="$emit('click')"
  >
    <!-- Background visual area / 视觉背景区域 -->
    <div class="card-visual">
      <div class="visual-placeholder">
        <el-icon class="header-icon"><Film /></el-icon>
      </div>
      <!-- Hover actions / 悬停操作区 -->
      <div class="hover-actions" @click.stop>
        <slot name="actions"></slot>
      </div>
    </div>

    <!-- Card content / 卡片内容 -->
    <div class="card-body">
      <div class="body-top">
        <h3 class="card-title">{{ title }}</h3>
        <p v-if="description" class="card-description">{{ description }}</p>
      </div>
      
      <!-- Footer section / 底部区域 -->
      <div class="card-footer">
        <div class="meta-tag">
          <span class="episode-count">{{ episodeCount }} 集</span>
        </div>
        <span class="meta-time">{{ formattedDate }}</span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Film } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  title: string
  description?: string
  updatedAt: string
  episodeCount?: number
}>(), {
  description: '',
  episodeCount: 0
})

defineEmits<{
  click: []
}>()

const formattedDate = computed(() => {
  const date = new Date(props.updatedAt)
  // 仅显示年月日，更符合简约风格
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
})
</script>

<style scoped>
.project-card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 20px; /* Large Apple-style radius */
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  width: 240px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.02);
  border: 1px solid rgba(0,0,0,0.04);
}

.project-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  border-color: rgba(0, 0, 0, 0.08);
}

/* Visual Area */
.card-visual {
  position: relative;
  height: 160px;
  background: #f5f5f7; /* Apple light gray */
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.visual-placeholder {
  transition: transform 0.6s ease;
}

.project-card:hover .visual-placeholder {
  transform: scale(1.1);
}

.header-icon {
  font-size: 40px;
  color: #d2d2d7;
}

/* Actions */
.hover-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.3s ease;
}

.project-card:hover .hover-actions {
  opacity: 1;
  transform: translateY(0);
}

/* Body Section */
.card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 140px;
}

.body-top {
  flex: 1;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 19px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.2;
}

.card-description {
  margin: 0;
  font-size: 14px;
  color: #86868b;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Footer Section */
.card-footer {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
}

.meta-tag {
  background: rgba(0, 122, 255, 0.08);
  padding: 4px 10px;
  border-radius: 6px;
}

.episode-count {
  font-size: 12px;
  font-weight: 600;
  color: #007aff;
}

.meta-time {
  font-size: 12px;
  color: #d2d2d7;
  font-weight: 500;
}

/* Dark mode */
.dark .project-card {
  background: #1d1d1f;
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.dark .card-visual {
  background: #2c2c2e;
}

.dark .card-title {
  color: #f5f5f7;
}

.dark .meta-tag {
  background: rgba(10, 132, 255, 0.15);
}

.dark .episode-count {
  color: #0a84ff;
}

:deep(.el-button.is-link) {
  padding: 8px;
  background: rgba(255, 255, 255, 0.9) !important;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
</style>
