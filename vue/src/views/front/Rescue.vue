<script setup>
import { ref, reactive, onMounted, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request.js'
import { regionData } from 'element-china-area-data'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from "@wangeditor/editor-for-vue"
import axios from "axios"
import { serverHost } from "../../../config/config.default.js"
import { Guide, WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()

const form = ref({})
const htmlContent = ref('')
const editorRefContent = shallowRef()

const showNoticeDialog = ref(true)

const agreeNotice = () => {
  showNoticeDialog.value = false
}

const disagreeNotice = () => {
  window.location.href = '/'
}

const save = () => {
  if (form.value.address) {
    form.value.address = form.value.address.join('/')
  } else {
    form.value.address = ''
  }
  form.value.content = htmlContent.value

  request.post('/rescue', form.value).then(res => {
    if (res.code === '200') {
      ElMessage.success('救助通报提交成功，我们会尽快处理')
      form.value = ''
      htmlContent.value = ''
      router.push('/front/myRescue')
    } else {
      ElMessage.error('提交失败，请稍后重试')
    }
  })
}

const customUpload = (file, insertFn) => {
  const formData = new FormData()
  formData.append('file', file)
  axios({
    url: `${serverHost}/web/upload`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then(res => {
    insertFn(res.data)
  }).catch((error) => {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  })
}

const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      customUpload: async (file, insertFn) => {
        customUpload(file, insertFn)
      },
    },
    uploadVideo: {
      customUpload: async (file, insertFn) => {
        customUpload(file, insertFn)
      },
    },
  }
}
</script>

<template>
  <div class="page-shell">
    <!-- 救助须知弹窗 -->
    <el-dialog
      v-model="showNoticeDialog"
      title="救助求助须知"
      width="620px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      class="notice-dialog"
    >
      <div class="notice-content">
        <div class="notice-title">
          <el-icon class="notice-icon"><WarningFilled /></el-icon>
          <span>请仔细阅读以下须知</span>
        </div>

        <div class="notice-item">
          <span class="notice-number">1</span>
          <div class="notice-text">
            <strong>请确保动物处于紧急危险状态</strong>
            <p>宠爱有家救助团队主要救助生命受到严重威胁的流浪动物，包括：严重车祸伤害、遭受虐待、重度疾病、被困危险环境等紧急情况。对于健康或轻微受伤的流浪动物，我们建议您联系当地动物保护组织或爱心人士进行帮助。</p>
          </div>
        </div>

        <div class="notice-item">
          <span class="notice-number">2</span>
          <div class="notice-text">
            <strong>求助人需配合救助全过程</strong>
            <p>为确保救助行动顺利进行，求助人需要提供准确详细的位置信息、动物状况描述，并在救助团队到达前保持与动物的安全距离。如条件允许，请协助救助人员进行现场引导。对于提供虚假信息或不配合救助的求助人，我们保留拒绝服务的权利。</p>
          </div>
        </div>

        <div class="notice-item">
          <span class="notice-number">3</span>
          <div class="notice-text">
            <strong>救助后的医疗及安置由团队统筹安排</strong>
            <p>获救动物将由宠爱有家指定的合作宠物医院进行专业治疗和康复照护。治疗方案、后续领养安排等事宜将由救助团队根据动物实际情况和专业评估做出决定，求助人如有意愿参与后续照护或领养，可提前向团队说明。</p>
          </div>
        </div>

        <div class="notice-item">
          <span class="notice-number">4</span>
          <div class="notice-text">
            <strong>救助资源有限，请理解我们的优先级安排</strong>
            <p>宠爱有家救助团队会尽全力响应每一个求助，但由于人力和资源有限，我们将根据动物的紧急程度、生命危险度、救助可行性等因素进行综合评估，优先处理最危险的案例。我们无法保证接收所有求助，敬请理解。</p>
          </div>
        </div>

        <div class="notice-footer">
          <p><strong>重要提示：</strong>当您点击"我同意"按钮后，即表示您已经完全理解并同意以上所有救助求助须知。如有任何疑问，请在提交求助前通过微信或电话联系我们。感谢您对流浪动物的关爱！</p>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="disagreeNotice">我不同意，返回首页</el-button>
          <el-button type="primary" size="large" @click="agreeNotice">我已仔细阅读并同意</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 救助通报表单 -->
    <div v-if="!showNoticeDialog" class="page-container narrow">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>救助中心</span>
        </div>
        <div class="head-row">
          <div>
            <h1>救助求助</h1>
            <p class="head-desc">
              感谢你对规则地尊重与认可。在救助社区里，你没有视而不见，你的努力让更多团队看到了这些问题。请用心填写细节，一起为动物福利而努力。
            </p>
          </div>
        </div>
      </div>

      <div class="content-card rescue-form">
        <el-form :model="form" label-position="top" size="large">
          <div class="form-section">
            <h3 class="form-section-title">救助标题</h3>
            <el-form-item label="救助标题" required>
              <el-input
                v-model="form.name"
                placeholder="请用25个字简要描述这则救助描述，如：日期某地区受虐待小狗"
                maxlength="25"
                show-word-limit
              />
            </el-form-item>
          </div>

          <div class="form-section">
            <h3 class="form-section-title">求助人基本信息</h3>

            <el-form-item label="求助人称呼" required>
              <el-input v-model="form.userName" placeholder="请输入您的称呼" />
            </el-form-item>

            <el-form-item label="手机号码" required>
              <el-input v-model="form.phone" placeholder="请输入您的手机号码" />
            </el-form-item>

            <el-form-item label="地址" required>
              <el-cascader
                v-model="form.address"
                :options="regionData"
                :props="{ value: 'label' }"
                clearable
                style="width: 100%"
                placeholder="请选择省/市/区"
              />
            </el-form-item>
          </div>

          <div class="form-section">
            <h3 class="form-section-title">救助求助内容</h3>
            <p class="section-desc">请务必详细填写求助内容，以便救助人员评估后顺利救助</p>

            <el-form-item label="通报类型" required>
              <el-select v-model="form.description" placeholder="请选择" style="width: 100%">
                <el-option label="被执行非法狗猫车" value="被执行非法狗猫车" />
                <el-option label="被执行非法屠宰场/囤狗点" value="被执行非法屠宰场/囤狗点" />
                <el-option label="遭异物勒颈（嘴/身体其他部位）" value="遭异物勒颈（嘴/身体其他部位）" />
                <el-option label="严重皮肤病" value="严重皮肤病" />
                <el-option label="车祸等意外伤害" value="车祸等意外伤害" />
                <el-option label="人为施虐" value="人为施虐" />
                <el-option label="自身其他严重疾病" value="自身其他严重疾病" />
                <el-option label="动物受困" value="动物受困" />
              </el-select>
            </el-form-item>

            <el-form-item label="情况描述" required>
              <div class="editor-wrap">
                <Toolbar
                  class="editor-toolbar"
                  :editor="editorRefContent"
                  :defaultConfig="editorConfig"
                  mode="default"
                />
                <Editor
                  class="editor-body"
                  v-model="htmlContent"
                  :defaultConfig="editorConfig"
                  mode="default"
                  @onCreated="editorRefContent = $event"
                />
              </div>
            </el-form-item>
          </div>

          <div class="form-submit">
            <el-button type="primary" size="large" class="submit-btn" @click="save">
              <el-icon class="btn-icon"><Guide /></el-icon>
              提交救助通报
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.page-container.narrow {
  max-width: 920px;
}

/* 须知弹窗 */
.notice-content {
  max-height: 58vh;
  overflow-y: auto;
  padding: 4px 8px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  color: var(--ink-900);
  margin-bottom: 20px;

  .notice-icon {
    color: var(--warn-500);
    font-size: 20px;
  }
}

.notice-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  .notice-number {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 13px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-top: 2px;
  }

  .notice-text {
    flex: 1;

    strong {
      display: block;
      font-size: 14px;
      color: var(--ink-900);
      margin-bottom: 6px;
    }

    p {
      margin: 0;
      font-size: 13px;
      line-height: 1.75;
      color: var(--ink-500);
    }
  }
}

.notice-footer {
  padding: 14px 16px;
  background: var(--warn-50);
  border-left: 4px solid var(--warn-500);
  border-radius: 8px;

  p {
    margin: 0;
    font-size: 13px;
    line-height: 1.75;
    color: var(--ink-700);
  }
}

/* 表单 */
.rescue-form {
  padding: 32px;
}

.form-section {
  padding-bottom: 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--line-light);

  &:last-of-type {
    border-bottom: none;
    margin-bottom: 0;
  }
}

.form-section-title {
  position: relative;
  margin: 0 0 20px;
  padding-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-900);

  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 2px;
    bottom: 2px;
    width: 4px;
    border-radius: 2px;
    background: var(--brand-500);
  }
}

.section-desc {
  margin: -8px 0 16px;
  font-size: 13px;
  color: var(--ink-500);
  line-height: 1.6;
}

.editor-wrap {
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  z-index: 100;
  width: 100%;

  .editor-toolbar {
    border-bottom: 1px solid var(--line);
  }

  .editor-body {
    height: 300px;
    overflow-y: hidden;
  }
}

.form-submit {
  display: flex;
  justify-content: center;
  padding-top: 8px;

  .submit-btn {
    min-width: 240px;
    height: 48px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 12px;
  }

  .btn-icon {
    margin-right: 4px;
  }
}
</style>
