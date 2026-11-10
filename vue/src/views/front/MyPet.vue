<script setup>
import { ref, reactive, shallowRef, onMounted } from 'vue'
import { Plus, Edit, Delete, UploadFilled, Present } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request.js'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import axios from "axios"
import { regionData } from 'element-china-area-data'
import { serverHost } from "../../../config/config.default.js"

const pets = ref([])
const loading = ref(false)

const form = ref({})
const dialogFormVisible = ref(false)

const account = ref(
  localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {}
)

const imgList = ref([])

const htmlContent = ref('')
const editorRefContent = shallowRef()

const healths = ref([])
const types = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const keyword = ref('')

const load = () => {
  loading.value = true
  request.get("/pet/my/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
    }
  }).then(res => {
    if (res.data) {
      pets.value = res.data.records || []
      total.value = res.data?.total || 0
    }
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

const loadHealth = () => {
  request.get('/health').then(res => {
    healths.value = res.data
  })
}

const loadType = () => {
  request.get('/type').then(res => {
    types.value = res.data
  })
}

const getHealthNames = (idsString) => {
  if (!idsString) return ''
  const idArray = idsString.split(',')
  const array = idArray.map(id => {
    const item = healths.value.find(item => item.id === id || item.id === Number(id))
    return item ? item.name : ''
  })
  return array.join(', ')
}

const getTypeName = (typeId) => {
  const type = types.value.find(item => item.id === typeId)
  return type ? type.name : ''
}

const handleAdd = () => {
  imgList.value = []
  htmlContent.value = ''
  form.value = {}
  dialogFormVisible.value = true
}

const handleEdit = (pet) => {
  form.value = JSON.parse(JSON.stringify(pet))

  if (form.value.health) {
    form.value.health = form.value.health.split(',').map(id => Number(id))
  } else {
    form.value.health = []
  }

  if (form.value.imgList) {
    imgList.value = form.value.imgList.split(',')
  } else {
    imgList.value = []
  }

  htmlContent.value = form.value.content || ''

  if (form.value.address) {
    form.value.address = form.value.address.split('/')
  } else {
    form.value.address = []
  }

  dialogFormVisible.value = true
}

const save = () => {
  if (!form.value.name) {
    ElMessage.warning('请输入宠物名称')
    return
  }
  if (!form.value.age) {
    ElMessage.warning('请输入宠物年龄')
    return
  }
  if (!form.value.sex) {
    ElMessage.warning('请选择宠物性别')
    return
  }
  if (!form.value.typeId) {
    ElMessage.warning('请选择宠物类别')
    return
  }
  if (!form.value.health) {
    ElMessage.warning('请选择健康状态')
    return
  }
  if (!form.value.address) {
    ElMessage.warning('请输入宠物所在地')
    return
  }
  if (!form.value.img) {
    ElMessage.warning('请上传宠物图片')
    return
  }

  if (form.value.health) {
    form.value.health = form.value.health.join(',')
  } else {
    form.value.health = ''
  }

  if (imgList.value.length > 0) {
    form.value.imgList = imgList.value.join(',')
  } else {
    form.value.imgList = ''
  }

  form.value.content = htmlContent.value

  if (form.value.address) {
    form.value.address = form.value.address.join('/')
  } else {
    form.value.address = ''
  }

  request.post("/pet", form.value).then(res => {
    if (res.code === '200') {
      ElMessage.success(form.value.id ? '更新成功' : '发布成功')
      dialogFormVisible.value = false
      load()
    } else {
      ElMessage.error(form.value.id ? '更新失败' : '发布失败')
    }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除这只宠物的领养信息吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    request.delete("/pet/" + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error("删除失败")
      }
    })
  })
}

const handleImgUploadSuccess = (res) => {
  form.value.img = res
}

const handleImgListUploadSuccess = (res) => {
  imgList.value.push(res)
}

const removeImgList = (index) => {
  imgList.value.splice(index, 1)
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
  placeholder: '请输入其他说明要求...',
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

const handleSizeChange = (newPageSize) => {
  pageSize.value = newPageSize
  load()
}

const handleCurrentChange = (newPageNum) => {
  pageNum.value = newPageNum
  load()
}

onMounted(() => {
  load()
  loadHealth()
  loadType()
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>我的宠物</span>
        </div>
        <div class="head-row">
          <div>
            <h1>我的宠物</h1>
            <p class="head-desc">管理你发布的宠物领养信息，发布新的毛孩子，或跟进申请进度。</p>
          </div>
          <el-button type="primary" round :icon="Plus" @click="handleAdd">
            发布新宠物
          </el-button>
        </div>
      </div>

      <div v-loading="loading" class="pet-grid">
        <div v-if="pets.length === 0 && !loading" class="content-card empty-block">
          <div class="empty-icon"><Present /></div>
          <p>还没有发布任何宠物</p>
          <el-button type="primary" round @click="handleAdd">立即发布</el-button>
        </div>

        <div v-for="pet in pets" :key="pet.id" class="pet-card">
          <div class="pet-media">
            <el-image :src="pet.img" fit="cover" :preview-src-list="[pet.img]" :preview-teleported="true" class="pet-img" />
            <div class="pet-status">
              <el-tag v-if="pet.status === '待领养'" type="success" effect="light">{{ pet.status }}</el-tag>
              <el-tag v-if="pet.status === '领养审核中'" type="warning" effect="light">{{ pet.status }}</el-tag>
              <el-tag v-if="pet.status === '已领养'" type="info" effect="light">{{ pet.status }}</el-tag>
            </div>
          </div>

          <div class="pet-info">
            <h3 class="pet-name">{{ pet.name }}</h3>

            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">年龄</span>
                <span class="value">{{ pet.age }}</span>
              </div>
              <div class="detail-item">
                <span class="label">性别</span>
                <el-tag size="small" :type="pet.sex === '公' ? 'primary' : 'danger'" effect="light">{{ pet.sex }}</el-tag>
              </div>
              <div class="detail-item" v-if="pet.typeId">
                <span class="label">类别</span>
                <span class="value">{{ getTypeName(pet.typeId) }}</span>
              </div>
              <div class="detail-item" v-if="pet.health">
                <span class="label">健康</span>
                <span class="value">{{ getHealthNames(pet.health) }}</span>
              </div>
              <div class="detail-item" v-if="pet.address">
                <span class="label">地址</span>
                <span class="value">{{ pet.address }}</span>
              </div>
              <div class="detail-item" v-if="pet.time">
                <span class="label">发布时间</span>
                <span class="value">{{ pet.time }}</span>
              </div>
            </div>

            <div class="pet-actions">
              <el-button type="primary" :icon="Edit" @click="handleEdit(pet)" plain :disabled="pet.status !== '待领养'">
                编辑
              </el-button>
              <el-button type="danger" :icon="Delete" @click="handleDelete(pet.id)" plain :disabled="pet.status !== '待领养'">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[9, 12, 15, 30]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total"
          background
        />
      </div>
    </div>

    <!-- 发布/编辑弹窗 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="form.id ? '编辑宠物信息' : '发布新宠物'"
      width="680px"
      :close-on-click-modal="false"
      class="pet-dialog"
    >
      <el-form :model="form" label-width="110px" label-position="left" size="large">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物名称" required>
              <el-input v-model="form.name" placeholder="请输入宠物名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" required>
              <el-input v-model="form.age" placeholder="例如：2岁、3个月" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" required>
              <el-radio-group v-model="form.sex">
                <el-radio value="公">公</el-radio>
                <el-radio value="母">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宠物类别" required>
              <el-select v-model="form.typeId" placeholder="请选择宠物类别" style="width: 100%" clearable>
                <el-option v-for="item in types" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状态" required>
              <el-select v-model="form.health" placeholder="请选择健康状态" style="width: 100%" multiple clearable>
                <el-option v-for="item in healths" :key="item.id" :label="item.name" :value="Number(item.id)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在地" required>
              <el-cascader
                v-model="form.address"
                :options="regionData"
                :props="{ value: 'label' }"
                clearable
                style="width: 100%"
                placeholder="请选择所在地"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="宠物照片" required>
          <div class="upload-container">
            <el-avatar v-if="form.img" :src="form.img" :size="80" shape="square" />
            <el-upload
              :action="`${serverHost}/web/upload`"
              :on-success="handleImgUploadSuccess"
              :show-file-list="false"
            >
              <el-button type="primary" :icon="UploadFilled">
                {{ form.img ? '更换照片' : '上传照片' }}
              </el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="更多照片">
          <div class="upload-container">
            <div class="image-list" v-if="imgList.length > 0">
              <div v-for="(img, index) in imgList" :key="index" class="image-item">
                <el-avatar :src="img" :size="64" shape="square" />
                <el-button
                  type="danger"
                  circle
                  :icon="Delete"
                  class="delete-btn"
                  @click="removeImgList(index)"
                />
              </div>
            </div>
            <el-upload
              :action="`${serverHost}/web/upload`"
              :on-success="handleImgListUploadSuccess"
              :show-file-list="false"
              multiple
            >
              <el-button type="primary" :icon="UploadFilled">上传更多照片</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="其他说明要求">
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
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" size="large" @click="save">
            {{ form.id ? '保存修改' : '立即发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.pet-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.pet-media {
  position: relative;
  height: 200px;
  background: var(--bg-subtle);

  .pet-img {
    width: 100%;
    height: 100%;
  }

  .pet-img :deep(img) {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .pet-status {
    position: absolute;
    top: 12px;
    right: 12px;
  }
}

.pet-info {
  padding: 18px;
}

.pet-name {
  margin: 0 0 14px;
  font-size: 19px;
  font-weight: 600;
  color: var(--ink-900);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-size: 13px;

  .label {
    color: var(--ink-400);
    flex-shrink: 0;
  }

  .value {
    color: var(--ink-700);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.pet-actions {
  display: flex;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid var(--line-light);

  .el-button {
    flex: 1;
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 8px;
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-item {
  position: relative;
}

.delete-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  transform: scale(0.75);
}

.editor-wrap {
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  overflow: hidden;
  width: 100%;

  .editor-toolbar {
    border-bottom: 1px solid var(--line);
  }

  .editor-body {
    height: 260px;
    overflow-y: hidden;
  }
}
</style>
