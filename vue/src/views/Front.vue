<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { projectName } from '../../config/config.default'
import {
  User,
  Lock,
  SwitchButton,
  Message,
  Promotion,
  Present,
  DataAnalysis,
  HomeFilled,
  Guide,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const account = ref(
  localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {}
)

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => account.value.role === 'ROLE_ADMIN')

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
  <div class="front-layout">
    <header class="front-header">
      <div class="header-inner">
        <div class="brand" @click="router.push('/front/home')">
          <img src="../../config/logo.svg" alt="Logo" class="brand-logo" />
          <span class="brand-name">{{ projectName }}</span>
        </div>

        <nav class="main-nav">
          <router-link
            v-for="item in [
              { path: '/front/home', label: '首页', icon: HomeFilled },
              { path: '/front/pet', label: '领养中心', icon: Present },
              { path: '/front/rescue', label: '救助中心', icon: Guide },
              { path: '/front/article', label: '宠物百科', icon: Promotion },
              { path: '/front/trend', label: '团队动态', icon: DataAnalysis }
            ]"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: activeMenu.startsWith(item.path) }"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <div class="header-user">
          <template v-if="!account.id">
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="primary" round @click="router.push('/register')">注册</el-button>
          </template>

          <template v-else>
            <el-button
              v-if="isAdmin"
              class="admin-entry"
              text
              @click="router.push('/back/home')"
            >
              管理后台
            </el-button>

            <el-dropdown trigger="click" class="user-dropdown">
              <div class="user-chip">
                <el-avatar :size="30" :src="account.avatarUrl" class="user-avatar">
                  {{ (account.nickname || account.username || 'U').slice(0, 1) }}
                </el-avatar>
                <span class="user-name">{{ account.nickname || account.username }}</span>
                <el-icon class="caret"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>
                    <router-link to="/front/myPet" class="dropdown-link">
                      <el-icon><Present /></el-icon>
                      <span>我的宠物</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link to="/front/myApplication" class="dropdown-link">
                      <el-icon><Message /></el-icon>
                      <span>我的领养申请</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link to="/front/myRescue" class="dropdown-link">
                      <el-icon><Guide /></el-icon>
                      <span>我的救助</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item divided>
                    <router-link to="/front/person" class="dropdown-link">
                      <el-icon><User /></el-icon>
                      <span>个人信息</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link to="/front/password" class="dropdown-link">
                      <el-icon><Lock /></el-icon>
                      <span>修改密码</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item divided>
                    <div @click="logout" class="dropdown-link logout-item">
                      <el-icon><SwitchButton /></el-icon>
                      <span>退出登录</span>
                    </div>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <main class="front-main">
      <router-view @update-account="handleUpdateAccount"></router-view>
    </main>

    <footer class="front-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <img src="../../config/logo.svg" alt="Logo" class="footer-logo" />
          <span>{{ projectName }}</span>
        </div>
        <p>以爱为名，用心守护 · 为流浪动物寻找温暖的家</p>
        <p class="copyright">© {{ new Date().getFullYear() }} {{ projectName }} · 保留所有权利</p>
      </div>
    </footer>
  </div>
</template>

<style lang="scss" scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.front-header {
  position: sticky;
  top: 0;
  z-index: 1200;
  height: var(--nav-h);
  background: #fff;
  border-bottom: 1px solid var(--line);
  display: flex;
  align-items: center;
}

.header-inner {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
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

  .brand-name {
    font-size: 19px;
    font-weight: 700;
    color: var(--brand-600);
    letter-spacing: 0.02em;
  }
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-item {
  position: relative;
  padding: 8px 14px;
  font-size: 14.5px;
  color: var(--ink-500);
  border-radius: 8px;
  transition: color 0.15s ease, background-color 0.15s ease;

  &:hover {
    color: var(--brand-600);
    background: var(--brand-50);
  }

  &.active {
    color: var(--brand-600);
    font-weight: 600;

    &::after {
      content: "";
      position: absolute;
      left: 14px;
      right: 14px;
      bottom: 2px;
      height: 2px;
      border-radius: 1px;
      background: var(--brand-500);
    }
  }
}

.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.admin-entry {
  color: var(--ink-500);
  font-size: 13.5px;
  padding: 6px 12px;
  border: 1px solid var(--line);
  border-radius: 8px;

  &:hover {
    color: var(--brand-600);
    border-color: var(--brand-300);
    background: var(--brand-50);
  }
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
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 13.5px;
    color: var(--ink-700);
  }

  .caret {
    color: var(--ink-400);
    font-size: 12px;
  }
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-700);
  width: 100%;
}

.logout-item {
  color: var(--danger-500);
  cursor: pointer;
}

.front-main {
  flex: 1;
}

.front-footer {
  background: #fff;
  border-top: 1px solid var(--line);
  padding: 28px 24px;

  .footer-inner {
    max-width: 1280px;
    margin: 0 auto;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
  }

  .footer-brand {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: var(--ink-700);

    .footer-logo {
      width: 22px;
      height: 22px;
    }
  }

  p {
    margin: 0;
    font-size: 12.5px;
    color: var(--ink-400);
  }

  .copyright {
    font-size: 12px;
  }
}
</style>
