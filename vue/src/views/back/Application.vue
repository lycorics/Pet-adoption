<script setup>
import { ref, reactive } from 'vue'
import { Search, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
  request.get("/application/page", {
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

const users = ref([])
const loadUser = () => {
  request.get('/user').then(res => {
    users.value = res.data
  })
}
loadUser()

const pets = ref([])
const loadPet = () => {
  request.get('/pet').then(res => {
    pets.value = res.data
  })
}
loadPet()

const ok = (id) => {
  request.get('/application/ok/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("已通过，请及时联系领养人进行线下见面签约")
    } else {
      ElMessage.error(res.msg)
    }
    load()
  })
}

const no = (id) => {
  request.get('/application/no/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("已拒绝该领养申请")
    } else {
      ElMessage.error(res.msg)
    }
    load()
  })
}

const detailDialogVisible = ref(false)
const currentDetail = ref({})

const viewDetail = (row) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}
</script>

<template>
  <div class="content-container">
    <div class="admin-head">
      <div>
        <h1>领养申请</h1>
        <p>集中处理领养申请，审核申请人资质并跟踪领养状态。</p>
      </div>
    </div>

    <div class="admin-table-card">
      <div class="toolbar-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入用户全名"
          class="filter-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
        />
        <el-button type="primary" plain @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="申请ID" width="90" align="center" />
        <el-table-column label="领养宠物" width="130" align="center">
          <template #default="scope">
            <span v-if="scope.row.petId">{{ pets.find(item => item.id === scope.row.petId)?.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请用户" width="120" align="center">
          <template #default="scope">
            <span v-if="scope.row.userId">{{ users.find(item => item.id === scope.row.userId)?.nickname }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="用户全名" width="110" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="age" label="年龄" width="70" align="center" />
        <el-table-column label="经济情况" width="140" align="center">
          <template #default="scope">
            <el-tag effect="light">{{ scope.row.economy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布方" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.category === '官方' ? 'primary' : 'info'" effect="light">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="110" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '待审核' ? 'warning' : scope.row.status === '已通过' ? 'success' : 'danger'"
              effect="light"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain size="small" :icon="View" @click="viewDetail(scope.row)">
              详情
            </el-button>
            <el-button
              type="success"
              size="small"
              v-if="scope.row.category === '官方' && scope.row.status === '待审核'"
              @click="ok(scope.row.id)"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              v-if="scope.row.category === '官方' && scope.row.status === '待审核'"
              @click="no(scope.row.id)"
            >
              拒绝
            </el-button>
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

    <!-- 申请详情 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="640px" :close-on-click-modal="false">
      <div class="detail-content" v-if="currentDetail">
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <div class="detail-grid">
            <div class="detail-item"><span class="label">申请ID</span><span class="value">{{ currentDetail.id }}</span></div>
            <div class="detail-item"><span class="label">用户全名</span><span class="value">{{ currentDetail.name }}</span></div>
            <div class="detail-item"><span class="label">性别</span><el-tag effect="light">{{ currentDetail.sex }}</el-tag></div>
            <div class="detail-item"><span class="label">年龄</span><span class="value">{{ currentDetail.age }} 岁</span></div>
            <div class="detail-item"><span class="label">联系电话</span><span class="value">{{ currentDetail.phone }}</span></div>
            <div class="detail-item"><span class="label">微信号</span><span class="value">{{ currentDetail.wechat }}</span></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">居住信息</h3>
          <div class="detail-grid">
            <div class="detail-item full"><span class="label">居住地址</span><span class="value">{{ currentDetail.address }}</span></div>
            <div class="detail-item full"><span class="label">具体地址</span><span class="value">{{ currentDetail.place }}</span></div>
            <div class="detail-item"><span class="label">住房类型</span><el-tag effect="light">{{ currentDetail.house }}</el-tag></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">家庭与经济状况</h3>
          <div class="detail-grid">
            <div class="detail-item full"><span class="label">家庭成员组成</span><span class="value">{{ currentDetail.family }}</span></div>
            <div class="detail-item"><span class="label">经济情况</span><el-tag effect="light">{{ currentDetail.economy }}</el-tag></div>
            <div class="detail-item"><span class="label">现有宠物</span><el-tag effect="light">{{ currentDetail.animal }}</el-tag></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">申请信息</h3>
          <div class="detail-grid">
            <div class="detail-item"><span class="label">申请用户</span><span class="value">{{ users.find(item => item.id === currentDetail.userId)?.nickname }}</span></div>
            <div class="detail-item"><span class="label">领养宠物</span><span class="value">{{ pets.find(item => item.id === currentDetail.petId)?.name }}</span></div>
            <div class="detail-item"><span class="label">审核状态</span>
              <el-tag
                :type="currentDetail.status === '待审核' ? 'warning' : currentDetail.status === '已通过' ? 'success' : 'danger'"
                effect="light"
              >
                {{ currentDetail.status }}
              </el-tag>
            </div>
            <div class="detail-item full"><span class="label">补充说明</span><p class="value-text">{{ currentDetail.instruction }}</p></div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            type="success"
            @click="ok(currentDetail.id); detailDialogVisible = false"
            v-if="currentDetail?.status === '待审核' && currentDetail?.category === '官方'"
          >
            通过申请
          </el-button>
          <el-button
            type="danger"
            @click="no(currentDetail.id); detailDialogVisible = false"
            v-if="currentDetail?.status === '待审核' && currentDetail?.category === '官方'"
          >
            拒绝申请
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.detail-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 4px 8px;
}

.detail-section {
  margin-bottom: 20px;
  padding: 16px;
  background: var(--bg-subtle);
  border-radius: var(--radius-md);

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  margin: 0 0 14px;
  font-size: 14.5px;
  font-weight: 600;
  color: var(--ink-900);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  line-height: 1.7;

  &.full {
    grid-column: 1 / -1;
  }

  .label {
    font-weight: 500;
    color: var(--ink-400);
    min-width: 84px;
    flex-shrink: 0;
  }

  .value {
    color: var(--ink-700);
    flex: 1;
  }

  .value-text {
    color: var(--ink-700);
    flex: 1;
    margin: 0;
    line-height: 1.6;
    white-space: pre-wrap;
  }
}
</style>
