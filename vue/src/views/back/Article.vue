<script setup>
import { ref, reactive, shallowRef } from 'vue'
import { Search, Plus, Delete, Edit, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'
import { serverHost } from '../../../config/config.default'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import axios from "axios"

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

const load = () => {
  request.get("/article/page", {
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
  form.value.content = htmlContent.value

  request.post("/article", form.value).then(res => {
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
  htmlContent.value = ''
  form.value = {}
  dialogFormVisible.value = true
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  htmlContent.value = form.value.content || ''
  dialogFormVisible.value = true
}

const del = (id) => {
  request.delete("/article/" + id).then(res => {
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
  request.post("/article/del/batch", ids).then(res => {
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

const handleImgUploadSuccess = (res) => {
  form.value.img = res
}

const htmlContent = ref('')
const editorRefContent = shallowRef()

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
</script>

<template>
  <div class="content-container">
    <div class="admin-head">
      <div>
        <h1>宠物百科</h1>
        <p>编写科学养宠知识文章，帮助用户更好地了解与照顾宠物。</p>
      </div>
    </div>

    <div class="admin-table-card">
      <div class="toolbar-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入标题"
          class="filter-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
        />
        <el-button type="primary" plain @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="toolbar-spacer"></div>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增文章</el-button>
        <el-button type="danger" plain :icon="Delete" @click="confirmBatchDelete">批量删除</el-button>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="文章" min-width="220">
          <template #default="scope">
            <div class="article-cell">
              <el-image class="article-thumb" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true" fit="cover" />
              <span class="article-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="时间" width="160" align="center" />
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

    <el-dialog
      v-model="dialogFormVisible"
      :title="form.id ? '编辑文章' : '新增文章'"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="100px" size="large">
        <el-form-item label="标题">
          <el-input v-model="form.name" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="封面">
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
        <el-form-item label="内容">
          <div class="editor-wrap">
            <Toolbar class="editor-toolbar" :editor="editorRefContent" :defaultConfig="editorConfig" mode="default" />
            <Editor class="editor-body" v-model="htmlContent" :defaultConfig="editorConfig" mode="default" @onCreated="editorRefContent = $event" />
          </div>
        </el-form-item>
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

.article-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .article-thumb {
    width: 56px;
    height: 40px;
    border-radius: 6px;
    flex-shrink: 0;
  }

  .article-name {
    font-weight: 500;
    color: var(--ink-900);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
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
    height: 300px;
    overflow-y: hidden;
  }
}
</style>
