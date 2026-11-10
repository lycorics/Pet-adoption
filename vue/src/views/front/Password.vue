<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Lock } from '@element-plus/icons-vue'

const router = useRouter()
const passFormRef = ref(null)

const form = reactive({
  password: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  password: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请输入确认密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ]
}

const save = () => {
  passFormRef.value.validate((valid) => {
    if (valid) {
      if (form.newPassword !== form.confirmPassword) {
        ElMessage.error('两次输入的新密码不相同')
        return false
      }

      request.post('/web/password', form).then(res => {
        if (res.code === '200') {
          ElMessage.success('密码修改成功')
          localStorage.removeItem('account')
          router.push('/login')
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}
</script>

<template>
  <div class="page-shell">
    <div class="page-container narrow">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>修改密码</span>
        </div>
        <div class="head-row">
          <div>
            <h1>修改密码</h1>
            <p class="head-desc">定期更换密码可以提高账户安全性。修改成功后需要重新登录。</p>
          </div>
        </div>
      </div>

      <div class="content-card password-card">
        <div class="card-icon">
          <el-icon><Lock /></el-icon>
        </div>

        <el-form
          ref="passFormRef"
          :model="form"
          :rules="rules"
          label-width="110px"
          class="password-form"
        >
          <el-form-item label="原密码" prop="password">
            <el-input
              v-model="form.password"
              placeholder="请输入当前密码"
              autocomplete="off"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="form.newPassword"
              placeholder="请输入新密码"
              autocomplete="off"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              placeholder="请再次输入新密码"
              autocomplete="off"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" @click="save">确认修改</el-button>
            <el-button size="large" @click="passFormRef.resetFields()">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="password-tips">
          <p>密码安全提示：</p>
          <ul>
            <li>密码长度至少 3 位字符</li>
            <li>建议使用字母、数字和特殊字符的组合</li>
            <li>定期更换密码可以提高账户安全性</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.page-container.narrow {
  max-width: 640px;
}

.password-card {
  padding: 36px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--brand-50);
  color: var(--brand-600);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 auto 24px;
}

.password-form {
  max-width: 440px;
  margin: 0 auto;
}

.password-tips {
  max-width: 440px;
  margin: 28px auto 0;
  padding-top: 20px;
  border-top: 1px dashed var(--line);

  p {
    margin: 0 0 10px;
    font-size: 13.5px;
    font-weight: 500;
    color: var(--ink-700);
  }

  li {
    position: relative;
    padding-left: 14px;
    margin-bottom: 6px;
    font-size: 12.5px;
    color: var(--ink-400);
    line-height: 1.7;

    &::before {
      content: "";
      position: absolute;
      left: 0;
      top: 8px;
      width: 5px;
      height: 5px;
      border-radius: 50%;
      background: var(--brand-300);
    }
  }
}
</style>
