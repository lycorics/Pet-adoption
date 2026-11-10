<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { projectName } from '../../config/config.default'
import {
  User,
  Lock,
  SwitchButton,
  ArrowDown,
  DataAnalysis
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const account = ref(
  localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {}
)

const isAdmin = computed(() => account.value.role === 'ROLE_ADMIN')

const activeMenu = computed(() => route.path)

const logout = () => {
  localStorage.removeItem('account')
  ElMessage.success('已安全退出')
  router.push('/login')
}

const handleUpdateAccount = (updatedAccount) => {
  account.value = updatedAccount
}
</script>

<template>
  <div class="admin-layout">
    <!-- 顶部固定导航 -->
    <header class="admin-header">
      <div class="header-brand" @click="router.push('/back/home')">
        <img src="../../config/logo.svg" alt="Logo" class="brand-logo" />
        <div class="brand-text">
          <span class="brand-name">{{ projectName }}</span>
          <span class="brand-sub">管理后台</span>
        </div>
      </div>

      <nav class="header-nav">
        <el-menu
          router
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          class="top-menu"
        >
          <el-menu-item index="/back/home">数据总览</el-menu-item>

          <el-sub-menu index="adopt-group">
            <template #title>领养管理</template>
            <el-menu-item index="/back/pet">宠物管理</el-menu-item>
            <el-menu-item index="/back/application">领养申请</el-menu-item>
            <el-menu-item index="/back/rescue">救助管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="content-group">
            <template #title>内容管理</template>
            <el-menu-item index="/back/trend">团队动态</el-menu-item>
            <el-menu-item index="/back/article">宠物百科</el-menu-item>
            <el-menu-item index="/back/comment">评论管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="data-group">
            <template #title>数据配置</template>
            <el-menu-item index="/back/type">宠物种类</el-menu-item>
            <el-menu-item index="/back/health">健康状态</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system-group" v-if="isAdmin">
            <template #title>系统管理</template>
            <el-menu-item index="/back/user">用户管理</el-menu-item>
            <el-menu-item index="/back/admin">管理员管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </nav>

      <div class="header-user">
        <el-dropdown trigger="click">
          <div class="user-chip">
            <el-avatar :size="30" :src="account.avatarUrl" class="user-avatar">
              {{ (account.nickname || account.username || 'A').slice(0, 1) }}
            </el-avatar>
            <span class="user-name">{{ account.nickname || account.username }}</span>
            <el-icon class="caret"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <router-link to="/back/adminPerson" class="dropdown-link">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/back/password" class="dropdown-link">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/home" class="dropdown-link">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>前往前台</span>
                </router-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-tooltip content="退出登录" placement="bottom">
          <button class="logout-btn" @click="logout">
            <el-icon><SwitchButton /></el-icon>
          </button>
        </el-tooltip>
      </div>
    </header>

    <!-- 内容区域（自动铺满） -->
    <main class="admin-content">
      <router-view @update-account="handleUpdateAccount"></router-view>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  background: var(--bg-page);
  display: flex;
  flex-direction: column;
}

/* 顶部导航 */
.admin-header {
  position: sticky;
  top: 0;
  z-index: 1100;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid var(--line);
  display: flex;
  align-items: center;
  gap: 28px;
  padding: 0 24px;
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;

  .brand-logo {
    width: 30px;
    height: 30px;
    object-fit: contain;
  }

  .brand-text {
    display: flex;
    flex-direction: column;
    line-height: 1.25;
  }

  .brand-name {
    font-size: 16px;
    font-weight: 700;
    color: var(--brand-600);
  }

  .brand-sub {
    font-size: 11px;
    color: var(--ink-400);
  }
}

/* 横向菜单 */
.header-nav {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  overflow-x: auto;
  overflow-y: hidden;

  .top-menu {
    border-bottom: none;
    background: transparent;
  }
}

.header-nav :deep(.el-menu--horizontal > .el-menu-item),
.header-nav :deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  height: 60px;
  line-height: 60px;
  border-bottom: none;
  color: var(--ink-500);
  transition: color 0.15s ease, background-color 0.15s ease;
}

.header-nav :deep(.el-menu--horizontal > .el-menu-item:hover),
.header-nav :deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title:hover) {
  color: var(--brand-600);
  background: var(--brand-50);
}

.header-nav :deep(.el-menu--horizontal > .el-menu-item.is-active),
.header-nav :deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title) {
  color: var(--brand-600);
  font-weight: 600;
  border-bottom: 2px solid var(--brand-500);
}

/* 用户区 */
.header-user {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border: 1px solid var(--line);
  border-radius: var(--radius-pill);
  cursor: pointer;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;

  &:hover {
    border-color: var(--brand-300);
    box-shadow: var(--shadow-sm);
  }

  .user-avatar {
    background: var(--brand-100);
    color: var(--brand-700);
    font-weight: 600;
  }

  .user-name {
    max-width: 110px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 13.5px;
    color: var(--ink-700);
  }

  .caret {
    font-size: 12px;
    color: var(--ink-400);
  }
}

.logout-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid var(--line);
  background: #fff;
  color: var(--ink-500);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.15s ease;

  &:hover {
    color: var(--danger-500);
    border-color: var(--danger-500);
    background: var(--danger-50);
  }
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-700);
  width: 100%;
}

/* 内容区 */
.admin-content {
  flex: 1;
  width: 100%;
  min-width: 0;
  padding: 20px;
}
</style>
