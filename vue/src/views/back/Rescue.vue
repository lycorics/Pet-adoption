<script setup>
import { ref, reactive } from 'vue'
import { Search, Delete, Guide } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'
import { regionData } from 'element-china-area-data'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
})

const multipleSelection = ref([])

const account = ref(localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {})

const load = () => {
  request.get("/rescue/page", {
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

const del = (id) => {
  request.delete("/rescue/" + id).then(res => {
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
  request.post("/rescue/del/batch", ids).then(res => {
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

const contentViewVisible = ref(false)
const currentViewContent = ref('')

const viewContent = (content) => {
  currentViewContent.value = content || ''
  contentViewVisible.value = true
}

const users = ref([])
const loadUser = () => {
  request.get('/user').then(res => {
    users.value = res.data
  })
}
loadUser()

const ok = (id) => {
  request.get('/rescue/ok/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("已经处理完成")
    } else {
      ElMessage.error(res.msg)
    }
    load()
  })
}

const no = (id) => {
  request.get('/rescue/no/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("取消成功")
    } else {
      ElMessage.error(res.msg)
    }
    load()
  })
}
</script>

<template>
  <div class="content-container">
    <div class="admin-head">
      <div>
        <h1>救助管理</h1>
        <p>处理用户提交的救助通报，跟进救援状态。</p>
      </div>
    </div>

    <div class="admin-table-card">
      <div class="toolbar-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入救助标题"
          class="filter-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
        />
        <el-button type="primary" plain @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="toolbar-spacer"></div>
        <el-button type="danger" plain :icon="Delete" @click="confirmBatchDelete">批量删除</el-button>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="救助标题" min-width="180" />
        <el-table-column prop="userName" label="通报人称呼" width="110" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="address" label="位置区域" min-width="130" />
        <el-table-column label="通报类型" width="150" align="center">
          <template #default="scope">
            <el-tag effect="light">{{ scope.row.description }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="发生时间" width="140" align="center" />
        <el-table-column label="内容" width="80" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="viewContent(scope.row.content)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="提交用户" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.userId">{{ users.find(item => item.id === scope.row.userId)?.nickname }}</span>
          </template>
        </el-table-column>
        <el-table-column label="处理状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '已救助' ? 'success' : scope.row.status === '已取消' ? 'info' : 'warning'"
              effect="light"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理" width="130" align="center" fixed="right">
          <template #default="scope">
            <el-button
              type="success"
              size="small"
              :icon="Guide"
              v-if="scope.row.status === '待救助'"
              @click="ok(scope.row.id)"
            >
              援助
            </el-button>
            <el-button
              type="danger"
              size="small"
              v-if="scope.row.status === '待救助'"
              @click="no(scope.row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="删除" width="80" align="center" fixed="right">
          <template #default="scope">
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

    <el-dialog v-model="contentViewVisible" title="救助详情" width="640px">
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
</style>
