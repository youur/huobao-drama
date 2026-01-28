<template>
  <div class="app-header-wrapper">
    <header class="app-header" :class="{ 'header-fixed': fixed, 'header-transparent': !fixed }">
      <div class="header-content">
        <!-- Left section: Logo + Left slot -->
        <div class="header-left">
          <router-link v-if="showLogo" to="/" class="logo">
            <span class="logo-text">影梦空间 ShadowDream</span>
          </router-link>
          <!-- Left slot for business content | 左侧插槽用于业务内容 -->
          <slot name="left" />
        </div>

        <!-- Center section: Center slot -->
        <div class="header-center">
          <slot name="center" />
        </div>

        <!-- Right section: Actions + Right slot -->
        <div class="header-right">
          
          <!-- Language Switcher | 语言切换 -->
          <LanguageSwitcher v-if="showLanguage" />
          
          <!-- Theme Toggle | 主题切换 -->
          <ThemeToggle v-if="showTheme" />
          
          <!-- AI Config (Model Switch) | AI 配置（模型切换） -->
          <el-button v-if="showAIConfig" @click="handleOpenAIConfig" class="header-btn glass-btn">
            <el-icon><Setting /></el-icon>
            <span class="btn-text">{{ $t('drama.aiConfig') }}</span>
          </el-button>
          <!-- Right slot for business content (before actions) | 右侧插槽（在操作按钮前） -->
          <slot name="right" />
        </div>
      </div>
    </header>
    
    <!-- AI Config Dialog | AI 配置对话框 -->
    <AIConfigDialog v-model="showConfigDialog" @config-updated="emit('config-updated')" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Setting } from '@element-plus/icons-vue'
import ThemeToggle from './ThemeToggle.vue'
import AIConfigDialog from './AIConfigDialog.vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

interface Props {
  fixed?: boolean
  showLogo?: boolean
  showLanguage?: boolean
  showTheme?: boolean
  showAIConfig?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  fixed: true,
  showLogo: true,
  showLanguage: true,
  showTheme: true,
  showAIConfig: true
})

const emit = defineEmits<{
  (e: 'open-ai-config'): void
  (e: 'config-updated'): void
}>()

const showConfigDialog = ref(false)

const handleOpenAIConfig = () => {
  showConfigDialog.value = true
  emit('open-ai-config')
}

defineExpose({
  openAIConfig: () => {
    showConfigDialog.value = true
  }
})
</script>

<style scoped>
.app-header {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 1000;
  transition: all 0.3s ease;
}

.app-header.header-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

.app-header.header-transparent {
  background: transparent;
  backdrop-filter: none;
  border-bottom: none;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 52px;
  max-width: 1400px; /* Consistent max-width */
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-shrink: 0;
}

.header-center {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.7;
}

.logo-text {
  font-weight: 600;
  font-size: 17px;
  color: #1d1d1f;
  letter-spacing: -0.01em;
}

.header-btn {
  border-radius: 999px;
  font-weight: 500;
  padding: 8px 16px;
  height: 32px;
  border: none;
}

.glass-btn {
  background: rgba(0, 0, 0, 0.04);
  color: #1d1d1f;
}

.glass-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  transform: scale(1.02);
}

/* Dark mode adjustments */
.dark .app-header:not(.header-transparent) {
  background: rgba(29, 29, 31, 0.72);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.dark .logo-text {
  color: #f5f5f7;
}

.dark .glass-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #f5f5f7;
}

.dark .glass-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

/* Slot Styles */
:deep(.page-title h1) {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.005em;
}

.dark :deep(.page-title h1) {
  color: #f5f5f7;
}

:deep(.page-title .subtitle) {
  font-size: 13px;
  color: #86868b;
  font-weight: 400;
}
</style>
