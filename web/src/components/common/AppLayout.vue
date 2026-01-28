<template>
  <div class="app-layout">
    <div class="ambient-background"></div>
    
    <!-- Global Header -->
    <header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <router-link to="/" class="logo">
            <span class="logo-text">影梦空间 ShadowDream</span>
          </router-link>
        </div>
        <div class="header-right">
          <LanguageSwitcher />
          <ThemeToggle />
          <el-button @click="showAIConfig = true" class="header-btn glass-btn">
            <el-icon><Setting /></el-icon>
            <span class="btn-text">{{ $t('drama.aiConfig') }}</span>
          </el-button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="app-main">
      <slot />
    </main>

    <!-- AI Config Dialog -->
    <AIConfigDialog v-model="showAIConfig" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Setting } from '@element-plus/icons-vue'
import ThemeToggle from './ThemeToggle.vue'
import AIConfigDialog from './AIConfigDialog.vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const showAIConfig = ref(false)
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  /* Apple-style base font */
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  background-color: #fbfbfd; /* Apple website light gray */
}

/* Ambient Background Effect */
.ambient-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  background: 
    radial-gradient(circle at 15% 50%, rgba(0, 122, 255, 0.03), transparent 25%),
    radial-gradient(circle at 85% 30%, rgba(88, 86, 214, 0.03), transparent 25%);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.72); /* High transparency */
  backdrop-filter: saturate(180%) blur(20px); /* Apple-style frost */
  border-bottom: 1px solid rgba(0, 0, 0, 0.05); /* Subtle border */
  transition: all 0.3s ease;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 52px; /* Slightly shorter, more elegant */
  max-width: 1400px;
  margin: 0 auto;
}

.header-btn {
  border-radius: 999px; /* Pill shape */
  font-weight: 500;
  padding: 8px 16px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.04);
  color: #1d1d1f;
  transition: all 0.2s ease;
}

.header-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  transform: scale(1.02);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
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
  font-size: 17px; /* Apple standard body size */
  color: #1d1d1f;
  letter-spacing: -0.01em;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-main {
  flex: 1;
  position: relative;
  z-index: 1;
}

/* Dark mode adjustments */
.dark .app-layout {
  background-color: #000000;
}

.dark .app-header {
  background: rgba(29, 29, 31, 0.72);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.dark .logo-text {
  color: #f5f5f7;
}

.dark .header-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #f5f5f7;
}

.dark .header-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}
</style>
