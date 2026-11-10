<script setup>
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { serverHost } from '../../../config/config.default'
import request from '@/utils/request'

const form = reactive({})

const account = ref(
  localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {}
)

const getAccount = () => {
  request.get('/web/userInfo').then(res => {
    if (res.code === '200' && res.data) {
      Object.assign(form, res.data)
    } else {
      ElMessage.error(res.msg)
    }
  })
}
getAccount()

const emit = defineEmits(['updateAccount'])

const save = () => {
  request.post('/user', form).then(res => {
    if (res.code === '200') {
      ElMessage.success('保存成功')

      if (form.nickname) account.value.nickname = form.nickname
      if (form.avatarUrl) account.value.avatarUrl = form.avatarUrl

      localStorage.setItem('account', JSON.stringify(account.value))
      emit('updateAccount', account.value)
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  })
}

const handleAvatarSuccess = (res) => {
  form.avatarUrl = res
}
</script>

<template>
  <div class="page-shell">
    <div class="page-container narrow">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>个人信息</span>
        </div>
        <div class="head-row">
          <div>
            <h1>个人信息</h1>
            <p class="head-desc">维护你的个人资料与头像，让其他爱心人士更好地认识你。</p>
          </div>
        </div>
      </div>

      <div class="content-card person-card">
        <div class="avatar-section">
          <el-upload
            :action="`${serverHost}/web/upload`"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            class="avatar-uploader"
          >
            <div class="avatar-box">
              <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar" />
              <el-icon v-else class="avatar-placeholder"><Plus /></el-icon>
            </div>
          </el-upload>
          <div class="avatar-tip">点击上传头像</div>
        </div>

        <el-form label-width="90px" class="person-form">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled autocomplete="off" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" autocomplete="off" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" autocomplete="off" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" autocomplete="off" placeholder="请输入电话" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="save">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.page-container.narrow {
  max-width: 640px;
}

.person-card {
  padding: 36px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 28px;
}

.avatar-box {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 2px dashed var(--line);
  background: var(--bg-subtle);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.15s ease;

  &:hover {
    border-color: var(--brand-500);
  }

  .avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-placeholder {
    font-size: 30px;
    color: var(--ink-300);
  }
}

.avatar-tip {
  margin-top: 10px;
  font-size: 12px;
  color: var(--ink-400);
}

.person-form {
  max-width: 420px;
  margin: 0 auto;
}
</style>
