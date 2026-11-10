<script setup>
import { ref, reactive } from 'vue'
import { Search, Plus, Delete, Edit, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { serverHost } from '../../../config/config.default'
import request from '../../utils/request'

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
  request.get("/admin/page", {
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
  request.post("/admin", form.value).then(res => {
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
  form.value = {}
  dialogFormVisible.value = true
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  dialogFormVisible.value = true
}

const del = (id) => {
  request.delete("/admin/" + id).then(res => {
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
  request.post("/admin/del/batch", ids).then(res => {
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

const handleAvatarUrlUploadSuccess = (res) => {
  form.value.avatarUrl = res
}
</script>

<template>
  <div class="content-container">
    <div class="admin-head">
      <div>
        <h1>管理员管理</h1>
        <p>维护后台管理员账号，分配管理权限。</p>
      </div>
    </div>

    <div class="admin-table-card">
      <div class="toolbar-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入昵称"
          class="filter-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
        />
        <el-button type="primary" plain @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="toolbar-spacer"></div>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增管理员</el-button>
        <el-button type="danger" plain :icon="Delete" @click="confirmBatchDelete">批量删除</el-button>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="管理员" min-width="180">
          <template #default="scope">
            <div class="user-cell">
              <el-avatar :size="40" :src="scope.row.avatarUrl" class="user-avatar">
                {{ (scope.row.nickname || scope.row.username || 'A').slice(0, 1) }}
              </el-avatar>
              <div class="user-cell-info">
                <span class="user-cell-name">{{ scope.row.nickname || '未设置昵称' }}</span>
                <span class="user-cell-meta">@{{ scope.row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="phone" label="电话" min-width="130" />
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
      :title="form.id ? '编辑管理员' : '新增管理员'"
      width="480px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="90px" size="large">
        <el-form-item label="头像" required>
          <div class="upload-container">
            <el-avatar v-if="form.avatarUrl" :src="form.avatarUrl" :size="64" />
            <el-upload
              :action="`${serverHost}/web/upload`"
              :on-success="handleAvatarUrlUploadSuccess"
              :show-file-list="false"
            >
              <el-button type="primary" plain :icon="UploadFilled">
                {{ form.avatarUrl ? '更换图片' : '上传图片' }}
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.toolbar-spacer {
  flex: 1;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-avatar {
    background: var(--brand-100);
    color: var(--brand-700);
    font-weight: 600;
    flex-shrink: 0;
  }

  .user-cell-info {
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .user-cell-name {
    font-weight: 600;
    color: var(--ink-900);
  }

  .user-cell-meta {
    font-size: 12px;
    color: var(--ink-400);
    margin-top: 2px;
  }
}
</style>
