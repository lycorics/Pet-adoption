<script setup>
import request from "@/utils/request.js"
import { ref, onMounted } from "vue"
import { Guide } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(() => {
  load()
})

const load = () => {
  request.get("/rescue/my/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  load()
}

const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}

const viewContent = (content) => {
  currentViewContent.value = content || ''
  contentViewVisible.value = true
}

const contentViewVisible = ref(false)
const currentViewContent = ref('')
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>我的救助</span>
        </div>
        <div class="head-row">
          <div>
            <h1>我的救助</h1>
            <p class="head-desc">查看你提交的救助通报与处理进度。</p>
          </div>
          <el-button type="primary" round :icon="Guide" @click="$router.push('/front/rescue')">
            发起新救助
          </el-button>
        </div>
      </div>

      <div class="content-card table-card">
        <el-table :data="tableData">
          <el-table-column prop="id" label="求助ID" width="90" />
          <el-table-column prop="name" label="救助标题" min-width="180" />
          <el-table-column prop="userName" label="通报人称呼" width="120" />
          <el-table-column prop="phone" label="联系电话" width="130" />
          <el-table-column prop="address" label="位置区域" min-width="150" />
          <el-table-column label="通报类型" width="130" align="center">
            <template #default="scope">
              <el-tag effect="light">{{ scope.row.description }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="发生时间" width="140" align="center" />
          <el-table-column label="情况描述" width="100" align="center">
            <template #default="scope">
              <el-button type="primary" plain @click="viewContent(scope.row.content)">查看</el-button>
            </template>
          </el-table-column>
          <el-table-column label="处理状态" width="110" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === '已救助' ? 'success' : scope.row.status === '已取消' ? 'info' : 'warning'"
                effect="light"
              >
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next"
            :total="total"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <el-dialog v-model="contentViewVisible" title="救助详情" width="640px" center>
      <div v-rich-img class="rich-content" v-html="currentViewContent"></div>
      <template #footer>
        <el-button @click="contentViewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.table-card {
  padding: 20px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
