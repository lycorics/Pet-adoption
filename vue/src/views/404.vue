<script setup>
import { useRouter } from 'vue-router'
import { projectName } from '../../config/config.default'
import { Present } from '@element-plus/icons-vue'

const router = useRouter()
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="not-found-page">
    <div class="nf-card">
      <div class="nf-icon"><Present /></div>
      <div class="error-code">404</div>
      <h1 class="error-title">页面未找到</h1>
      <p class="error-message">抱歉，你访问的页面不存在或已被移除</p>
      <el-button type="primary" size="large" round @click="goToLogin">返回登录页</el-button>
      <p class="brand-footer">© {{ new Date().getFullYear() }} {{ projectName }} · 保留所有权利</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-page);
  padding: 24px;
}

.nf-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 16px;
  box-shadow: var(--shadow-md);
  padding: 56px 64px;
  text-align: center;
  max-width: 460px;
}

.nf-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: var(--brand-50);
  color: var(--brand-500);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.error-code {
  font-size: 52px;
  font-weight: 800;
  color: var(--ink-900);
  line-height: 1;
  letter-spacing: 0.04em;
}

.error-title {
  margin: 14px 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: var(--ink-900);
}

.error-message {
  margin: 0 0 28px;
  font-size: 14px;
  color: var(--ink-500);
}

.brand-footer {
  margin: 28px 0 0;
  font-size: 12px;
  color: var(--ink-400);
}
</style>
