<script setup>
import { ref, reactive, shallowRef } from 'vue'
import { Search, Plus, Delete, Edit, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'
import { serverHost } from '../../../config/config.default'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import axios from "axios"
import { regionData } from 'element-china-area-data'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
})

const form = ref({})
const dialogFormVisible = ref(false)
const multipleSelection = ref([])

const account = ref(localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {})

const load = () => {
  request.get("/pet/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  })
}
load()

const save = () => {
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
      ElMessage.success("保存成功")
      dialogFormVisible.value = false
      load()
    } else {
      ElMessage.error("保存失败")
    }
  })
}

const handleAdd = () => {
  imgList.value = []
  htmlContent.value = ''
  form.value = {}
  dialogFormVisible.value = true
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row))

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

const del = (id) => {
  request.delete("/pet/" + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("删除成功")
      load()
    } else {
      ElMessage.error("删除失败")
    }
  })
}

const delBatch = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }

  const ids = multipleSelection.value.map(v => v.id)
  request.post("/pet/del/batch", ids).then(res => {
    if (res.code === '200') {
      ElMessage.success("批量删除成功")
      load()
    } else {
      ElMessage.error("批量删除失败")
    }
  })
}

const reset = () => {
  searchForm.keyword = ""
  load()
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handleSizeChange = (size) => {
  pageSize.value = size
  load()
}

const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}

const confirmDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      del(id)
    })
}

const confirmBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }

  ElMessageBox.confirm(
    '确定要批量删除这些数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      delBatch()
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

const handleImgUploadSuccess = (res) => {
  form.value.img = res
}

const imgList = ref([])

const handleImgListUploadSuccess = (res) => {
  imgList.value.push(res)
}

const removeImgList = (index) => {
  imgList.value.splice(index, 1)
}

const htmlContent = ref('')
const editorRefContent = shallowRef()

const getImageList = (imgString) => {
  if (!imgString) return []
  return imgString.split(',')
}

const contentViewVisible = ref(false)
const currentViewContent = ref('')

const viewContent = (content) => {
  currentViewContent.value = content || ''
  contentViewVisible.value = true
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

const healths = ref([])
const loadHealth = () => {
  request.get('/health').then(res => {
    healths.value = res.data
  })
}
loadHealth()

const types = ref([])
const loadType = () => {
  request.get('/type').then(res => {
    types.value = res.data
  })
}
loadType()
</script>

<template>
  <div class="content-container">
    <div class="admin-head">
      <div>
        <h1>宠物管理</h1>
        <p>管理平台全部宠物信息，支持新增、编辑、删除与批量操作。</p>
      </div>
    </div>

    <div class="admin-table-card">
      <div class="toolbar-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入宠物名称"
          class="filter-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
        />
        <el-button type="primary" plain @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="toolbar-spacer"></div>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增宠物</el-button>
        <el-button type="danger" plain :icon="Delete" @click="confirmBatchDelete">批量删除</el-button>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="宠物" min-width="180">
          <template #default="scope">
            <div class="pet-cell">
              <el-image
                class="pet-thumb"
                :src="scope.row.img"
                :preview-src-list="[scope.row.img]"
                :preview-teleported="true"
                fit="cover"
              />
              <div class="pet-cell-info">
                <span class="pet-cell-name">{{ scope.row.name }}</span>
                <span class="pet-cell-meta">
                  {{ types.find(item => item.id === scope.row.typeId)?.name || '未知品种' }} · {{ scope.row.age }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.sex === '公' ? 'primary' : 'danger'"
              effect="light"
            >
              {{ scope.row.sex }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="健康状态" min-width="160" align="center">
          <template #default="scope">
            <span v-if="scope.row.health" class="health-text">{{ getHealthNames(scope.row.health) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="所在地" min-width="130" />
        <el-table-column label="发布方" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.category === '官方' ? 'primary' : 'info'" effect="light">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="领养状态" width="110" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '待领养' ? 'success' : scope.row.status === '领养审核中' ? 'warning' : 'info'"
              effect="light"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="发布时间" width="140" align="center" />
        <el-table-column label="内容" width="80" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="viewContent(scope.row.content)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button circle type="primary" plain :icon="Edit" @click="handleEdit(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button circle type="danger" plain :icon="Delete" @click="confirmDelete(scope.row.id)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="form.id ? '编辑宠物' : '新增宠物'"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="100px" size="large">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物名称">
              <el-input v-model="form.name" placeholder="请输入宠物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="form.age" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.sex">
                <el-radio :value="'公'">公</el-radio>
                <el-radio :value="'母'">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宠物类别">
              <el-select v-model="form.typeId" placeholder="请选择宠物类别" style="width: 100%">
                <el-option v-for="item in types" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="健康状态">
              <el-select v-model="form.health" placeholder="请选择健康状态" style="width: 100%" multiple>
                <el-option v-for="item in healths" :key="item.id" :label="item.name" :value="Number(item.id)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所在地">
              <el-cascader
                v-model="form.address"
                :options="regionData"
                :props="{ value: 'label' }"
                clearable
                style="width: 100%"
                placeholder="请输入所在地"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图">
              <div class="upload-container">
                <el-avatar v-if="form.img" :src="form.img" :size="64" shape="square" />
                <el-upload
                  :action="`${serverHost}/web/upload`"
                  :on-success="handleImgUploadSuccess"
                  :show-file-list="false"
                >
                  <el-button type="primary" plain :icon="UploadFilled">
                    {{ form.img ? '更换图片' : '上传图片' }}
                  </el-button>
                </el-upload>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="更多图片">
              <div class="upload-container">
                <div class="image-list" v-if="imgList.length > 0">
                  <div v-for="(img, index) in imgList" :key="index" class="image-item">
                    <el-avatar :src="img" :size="56" shape="square" />
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
                  <el-button type="primary" plain :icon="UploadFilled">上传图片</el-button>
                </el-upload>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="其他说明">
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
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="contentViewVisible" title="内容详情" width="640px">
      <div v-rich-img class="rich-content" v-html="currentViewContent"></div>
      <template #footer>
        <el-button @click="contentViewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.toolbar-spacer {
  flex: 1;
}

.pet-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .pet-thumb {
    width: 46px;
    height: 46px;
    border-radius: 8px;
    flex-shrink: 0;
  }

  .pet-cell-info {
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .pet-cell-name {
    font-weight: 600;
    color: var(--ink-900);
  }

  .pet-cell-meta {
    font-size: 12px;
    color: var(--ink-400);
    margin-top: 2px;
  }
}

.health-text {
  font-size: 13px;
  color: var(--teal-600);
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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
