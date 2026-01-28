<template>
  <div :class="['empty-state', `size-${size}`]">
    <div class="empty-icon">
      <el-icon :size="iconSize">
        <component :is="icon" />
      </el-icon>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-description">{{ description }}</p>
    <div v-if="$slots.default" class="empty-actions">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'
import { FolderOpened } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  title: string
  description?: string
  icon?: Component
  size?: 'small' | 'medium' | 'large'
}>(), {
  icon: FolderOpened,
  size: 'medium'
})

const iconSize = computed(() => {
  const sizes = {
    small: 40,
    medium: 56,
    large: 72
  }
  return sizes[props.size]
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 64px 24px;
  background: transparent;
}

/* Icon */
.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: #86868b; /* Apple muted gray */
  opacity: 0.8;
  transition: transform 0.3s ease;
}

.empty-state:hover .empty-icon {
  transform: scale(1.05);
  color: #007aff; /* Apple Blue on hover */
}

/* Title */
.empty-title {
  margin: 0 0 12px 0;
  font-size: 21px; /* SF Pro Display size */
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: 0.01em;
}

/* Description */
.empty-description {
  margin: 0;
  font-size: 16px;
  color: #86868b;
  max-width: 400px;
  line-height: 1.5;
  font-weight: 400;
}

/* Actions */
.empty-actions {
  margin-top: 32px;
  display: flex;
  gap: 16px;
}

/* Dark mode */
.dark .empty-icon {
  color: #6e6e73;
}

.dark .empty-state:hover .empty-icon {
  color: #0a84ff;
}

.dark .empty-title {
  color: #f5f5f7;
}

.dark .empty-description {
  color: #86868b;
}
</style>
