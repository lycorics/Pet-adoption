<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { projectName } from '../../config/config.default'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from "@/utils/request.js"

const router = useRouter()
const userFormInst = ref(null)

const roleOptions = [
  { label: '普通用户', value: 'ROLE_USER' },
  { label: '管理员', value: 'ROLE_ADMIN' }
]

const account = reactive({
  username: '',
  password: '',
  role: 'ROLE_USER'
})

const isAllow = ref(true)

const roleClass = computed(() => (account.role === 'ROLE_ADMIN' ? 'is-admin' : 'is-user'))
const roleLabel = computed(() => (account.role === 'ROLE_ADMIN' ? '管理员登录' : '普通用户登录'))

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const login = () => {
  userFormInst.value.validate((valid) => {
    if (valid) {
      request.post('/web/login', account).then((res) => {
        if (res.code === '200') {
          localStorage.setItem('account', JSON.stringify(res.data))
          if (res.data.role === 'ROLE_ADMIN') {
            router.push('/back/home')
          } else {
            router.push('/front/home')
          }
          ElMessage.success('登录成功')
        } else {
          ElMessage.error(res.msg || '出错了')
        }
      })
    }
  })
}
</script>

<template>
  <div class="auth-page" :class="roleClass">
    <!-- 背景层：随角色切换 -->
    <div class="bg-layer bg-user"></div>
    <div class="bg-layer bg-admin"></div>

    <div class="auth-wrap">
      <!-- 居中白色卡片 -->
      <div class="auth-card">
        <div class="card-brand">
          <img src="../../config/logo.svg" alt="Logo" class="brand-logo" />
          <h1 class="card-title">欢迎回来</h1>
          <p class="card-subtitle">登录你的账号，继续你的爱心之旅</p>
          <span class="role-badge">
            <i class="role-dot"></i>
            {{ roleLabel }}
          </span>
        </div>

        <el-form
          :model="account"
          :rules="rules"
          ref="userFormInst"
          class="auth-form"
          @keydown.enter="login"
        >
          <el-form-item prop="username">
            <el-input
              placeholder="请输入用户名"
              :prefix-icon="User"
              v-model="account.username"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              :prefix-icon="Lock"
              show-password
              placeholder="请输入密码"
              v-model="account.password"
            />
          </el-form-item>

          <el-form-item prop="role">
            <el-select v-model="account.role" placeholder="请选择角色" class="role-select">
              <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="isAllow" class="allow-warp">
              我已阅读并同意
              <a href="#" class="link">《隐私政策》</a>
              和
              <a href="#" class="link">《服务条款》</a>
            </el-checkbox>
          </el-form-item>

          <el-button class="auth-button" type="primary" :disabled="!isAllow" @click="login">
            登 录
          </el-button>
        </el-form>

        <div class="auth-switch">
          还没有账号？
          <a @click="router.push('/register')" class="link">立即注册</a>
        </div>
      </div>

      <!-- 版权信息 -->
      <p class="auth-copyright">© {{ new Date().getFullYear() }} {{ projectName }} · 保留所有权利</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  overflow: hidden;
}

/* ---- 背景层（双渐变交叉淡入） ---- */
.bg-layer {
  position: absolute;
  inset: 0;
  transition: opacity 0.5s ease;
}

.bg-user {
  opacity: 1;
  background:
    radial-gradient(circle at 82% 18%, rgba(255, 255, 255, 0.85) 0%, rgba(255, 255, 255, 0) 42%),
    radial-gradient(circle at 12% 88%, rgba(238, 127, 91, 0.14) 0%, rgba(238, 127, 91, 0) 46%),
    linear-gradient(155deg, #f9e8dc 0%, #fdf3ec 48%, #f4dfce 100%);
}

.bg-admin {
  opacity: 0;
  background:
    radial-gradient(circle at 82% 14%, rgba(232, 97, 60, 0.22) 0%, rgba(232, 97, 60, 0) 42%),
    radial-gradient(circle at 10% 90%, rgba(78, 110, 140, 0.28) 0%, rgba(78, 110, 140, 0) 48%),
    linear-gradient(155deg, #2d3c4c 0%, #1e2937 55%, #151d28 100%);
}

.is-admin .bg-user {
  opacity: 0;
}

.is-admin .bg-admin {
  opacity: 1;
}

/* ---- 居中内容 ---- */
.auth-wrap {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

/* ---- 卡片 ---- */
.auth-card {
  width: 460px;
  max-width: calc(100vw - 40px);
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 24px 64px rgba(16, 24, 40, 0.12), 0 2px 8px rgba(16, 24, 40, 0.04);
  border: 1px solid rgba(16, 24, 40, 0.04);
  padding: 44px 44px 34px;
}

.card-brand {
  text-align: center;
  margin-bottom: 28px;

  .brand-logo {
    width: 52px;
    height: 52px;
    object-fit: contain;
    margin-bottom: 18px;
  }

  .card-title {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 700;
    color: var(--ink-900);
    letter-spacing: -0.01em;
  }

  .card-subtitle {
    margin: 0 0 18px;
    font-size: 14px;
    color: var(--ink-500);
  }

  .role-badge {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    padding: 5px 14px;
    border-radius: var(--radius-pill);
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 12.5px;
    font-weight: 600;

    .role-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--brand-500);
    }
  }
}

/* ---- 表单 ---- */
.auth-form {
  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  /* 输入框：50px / 12px 圆角 */
  :deep(.el-input__wrapper) {
    height: 50px;
    border-radius: 12px;
    padding: 0 16px;
    box-shadow: 0 0 0 1px var(--line) inset;
    transition: box-shadow 0.18s ease;
  }

  :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px var(--ink-300) inset;
  }

  :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px var(--brand-500) inset, 0 0 0 3px var(--brand-50);
  }

  /* 角色选择器：与输入框同高同圆角 */
  :deep(.el-select__wrapper) {
    min-height: 50px;
    border-radius: 12px;
    padding: 0 16px;
    box-shadow: 0 0 0 1px var(--line) inset;
    transition: box-shadow 0.18s ease;
  }

  :deep(.el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px var(--ink-300) inset;
  }

  :deep(.el-select__wrapper.is-focused) {
    box-shadow: 0 0 0 1px var(--brand-500) inset, 0 0 0 3px var(--brand-50);
  }

  .allow-warp {
    font-size: 13px;
    color: var(--ink-500);
  }

  .auth-button {
    width: 100%;
    height: 52px;
    margin-top: 4px;
    border-radius: 12px;
    font-size: 15.5px;
    font-weight: 600;
    letter-spacing: 0.18em;
    box-shadow: 0 8px 20px rgba(232, 97, 60, 0.22);
  }
}

.auth-switch {
  margin-top: 24px;
  text-align: center;
  font-size: 13.5px;
  color: var(--ink-500);
}

.link {
  color: var(--brand-600);
  font-weight: 500;
  cursor: pointer;
  transition: color 0.15s ease;

  &:hover {
    color: var(--brand-700);
  }
}

.auth-copyright {
  margin: 28px 0 0;
  font-size: 12.5px;
  color: var(--ink-400);
  text-align: center;
}

@media (max-width: 520px) {
  .auth-card {
    padding: 34px 26px 28px;
  }
}
</style>
