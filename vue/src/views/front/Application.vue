<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'
import { regionData } from 'element-china-area-data'
import { Present, InfoFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const form = ref({})

const id = route.query.id

const pet = ref({})
const load = () => {
  request.get("/pet/" + id).then(res => {
    pet.value = res.data
  })
}

onMounted(() => {
  load()
  window.scrollTo(0, 0)
})

const validateForm = () => {
  if (!form.value.name) {
    ElMessage.warning('请输入您的全名')
    return false
  }
  if (!form.value.address) {
    ElMessage.warning('请选择您的居住地区')
    return false
  }
  if (!form.value.place) {
    ElMessage.warning('请输入详细地址')
    return false
  }
  if (!form.value.phone) {
    ElMessage.warning('请输入您的电话')
    return false
  }
  if (!form.value.wechat) {
    ElMessage.warning('请输入您的微信号')
    return false
  }
  if (!form.value.sex) {
    ElMessage.warning('请选择您的性别')
    return false
  }
  if (!form.value.age) {
    ElMessage.warning('请输入您的年龄')
    return false
  }
  if (!form.value.house) {
    ElMessage.warning('请选择您的住宅类型')
    return false
  }
  if (!form.value.economy) {
    ElMessage.warning('请选择您的经济情况')
    return false
  }
  if (!form.value.animal) {
    ElMessage.warning('请选择家中是否有其他宠物')
    return false
  }
  return true
}

const save = () => {
  if (!validateForm()) {
    return
  }

  if (form.value.address) {
    form.value.address = form.value.address.join('/');
  } else {
    form.value.address = '';
  }

  form.value.petId = id

  request.post("/application", form.value).then(res => {
    if (res.code === '200') {
      ElMessage.success('申请提交成功！我们会尽快与您联系。')
      load()
      form.value = {}
      router.push('/front/myApplication')
    } else {
      ElMessage.error('提交失败，请稍后重试')
    }
  })
}
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <router-link to="/front/pet">领养中心</router-link>
          <span class="sep">/</span>
          <span>领养申请</span>
        </div>
        <div class="head-row">
          <div>
            <h1>领养申请</h1>
            <p class="head-desc">
              我们希望每一只毛孩子都能进入负责任的家庭。请认真填写以下信息，这是我们评估领养资质的重要依据。
            </p>
          </div>
        </div>
      </div>

      <div class="apply-layout">
        <!-- 左：表单 -->
        <div class="form-col">
          <div class="content-card form-card">
            <el-form :model="form" label-position="top" size="large">
              <div class="form-section">
                <h3 class="form-section-title">基本信息</h3>

                <el-form-item label="全名" required>
                  <el-input v-model="form.name" placeholder="请输入您的全名" clearable />
                </el-form-item>

                <el-form-item label="性别 / 年龄" required>
                  <div class="row-group">
                    <el-select v-model="form.sex" placeholder="请选择性别" style="width: 180px">
                      <el-option label="男" value="男" />
                      <el-option label="女" value="女" />
                    </el-select>
                    <el-input v-model="form.age" placeholder="请输入您的年龄" clearable style="width: 180px" />
                  </div>
                </el-form-item>

                <el-form-item label="你的电话" required>
                  <el-input v-model="form.phone" placeholder="请输入您的电话" clearable style="max-width: 320px" />
                </el-form-item>

                <el-form-item label="微信号" required>
                  <el-input v-model="form.wechat" placeholder="请输入您的微信号" clearable style="max-width: 320px" />
                </el-form-item>
              </div>

              <div class="form-section">
                <h3 class="form-section-title">居住情况</h3>

                <el-form-item label="未来您会带宠物共同居住的地址" required>
                  <div class="row-group">
                    <el-cascader
                      v-model="form.address"
                      :options="regionData"
                      :props="{ value: 'label' }"
                      clearable
                      style="width: 260px"
                      placeholder="请选择居住地区"
                    />
                    <el-input v-model="form.place" placeholder="请输入您的详细地址" clearable style="flex: 1" />
                  </div>
                </el-form-item>

                <el-form-item label="你的住宅类型" required>
                  <el-radio-group v-model="form.house">
                    <el-radio label="别墅">别墅</el-radio>
                    <el-radio label="普通住宅">普通住宅</el-radio>
                    <el-radio label="乡村庭院">乡村庭院</el-radio>
                    <el-radio label="农场">农场</el-radio>
                    <el-radio label="其他场所">其他场所</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>

              <div class="form-section">
                <h3 class="form-section-title">家庭情况</h3>

                <el-form-item label="您的经济情况" required>
                  <el-radio-group v-model="form.economy">
                    <el-radio label="无收入">无收入</el-radio>
                    <el-radio label="职业稳定/退休">职业稳定/退休</el-radio>
                    <el-radio label="家庭/共同经济">家庭/共同经济</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="现在家里是否有其他宠物" required>
                  <el-radio-group v-model="form.animal">
                    <el-radio label="有狗">有狗</el-radio>
                    <el-radio label="有猫">有猫</el-radio>
                    <el-radio label="没有宠物">没有宠物</el-radio>
                    <el-radio label="其他宠物">其他宠物</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="家庭成员组成">
                  <el-input
                    v-model="form.family"
                    type="textarea"
                    :rows="3"
                    placeholder="请简要描述您的家庭成员情况"
                  />
                </el-form-item>
              </div>

              <div class="form-section">
                <h3 class="form-section-title">其他申请补充</h3>

                <el-form-item label="补充说明">
                  <el-input
                    v-model="form.instruction"
                    type="textarea"
                    :rows="5"
                    placeholder="请填写其他需要补充的信息（如：养宠经验、领养原因等）"
                  />
                </el-form-item>
              </div>

              <div class="form-submit">
                <el-button type="primary" size="large" class="submit-btn" @click="save">
                  <el-icon class="btn-icon"><Present /></el-icon>
                  提交申请
                </el-button>
                <p class="submit-tip">提交后可在「我的领养申请」中查看审核进度</p>
              </div>
            </el-form>
          </div>
        </div>

        <!-- 右：宠物信息 -->
        <div class="side-col">
          <div class="content-card pet-summary">
            <div class="summary-media">
              <img :src="pet.img" :alt="pet.name" class="summary-img" />
            </div>
            <div class="summary-body">
              <span class="summary-badge">{{ pet.category || '个人发布' }}</span>
              <h3 class="summary-name">{{ pet.name }}</h3>
              <div class="summary-meta">
                <span>{{ pet.sex || '未知' }}</span>
                <span>{{ pet.age || '未知' }}</span>
                <span>{{ pet.address || '待确认' }}</span>
              </div>
            </div>
          </div>

          <div class="content-card tip-card">
            <div class="tip-head">
              <el-icon class="tip-icon"><InfoFilled /></el-icon>
              <span>申请须知</span>
            </div>
            <ul class="tip-list">
              <li>请如实填写信息，我们会进行领养资质审核</li>
              <li>审核通过后，请及时联系线下见面与签领</li>
              <li>领养不是一时冲动，请确认家人与居住环境合适</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.apply-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
  align-items: start;
}

.form-card {
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
  margin: 0 0 22px;
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

.row-group {
  display: flex;
  gap: 12px;
  width: 100%;
}

.form-submit {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding-top: 20px;

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

  .submit-tip {
    margin: 0;
    font-size: 12.5px;
    color: var(--ink-400);
  }
}

/* 侧栏 */
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: calc(var(--nav-h) + 20px);
}

.pet-summary {
  overflow: hidden;

  .summary-media {
    height: 180px;
    overflow: hidden;
    background: var(--bg-subtle);

    .summary-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .summary-body {
    padding: 16px;
  }

  .summary-badge {
    display: inline-block;
    padding: 3px 10px;
    border-radius: var(--radius-pill);
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 12px;
    font-weight: 600;
    margin-bottom: 8px;
  }

  .summary-name {
    margin: 0 0 10px;
    font-size: 20px;
    font-weight: 700;
    color: var(--ink-900);
  }

  .summary-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    span {
      padding: 3px 10px;
      border-radius: var(--radius-sm);
      background: var(--bg-subtle);
      font-size: 12.5px;
      color: var(--ink-500);
    }
  }
}

.tip-card {
  padding: 18px 20px;

  .tip-head {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 600;
    color: var(--ink-900);
    margin-bottom: 12px;

    .tip-icon {
      color: var(--brand-500);
    }
  }

  .tip-list {
    li {
      position: relative;
      padding-left: 14px;
      margin-bottom: 8px;
      font-size: 12.5px;
      line-height: 1.6;
      color: var(--ink-500);

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

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

@media (max-width: 900px) {
  .apply-layout {
    grid-template-columns: 1fr;
  }

  .side-col {
    position: static;
    order: -1;
  }
}
</style>
