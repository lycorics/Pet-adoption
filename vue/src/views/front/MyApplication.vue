<script setup>
import request from "@/utils/request.js"
import { ref, onMounted, computed } from "vue"
import { regionData } from 'element-china-area-data'
import { ElMessage } from "element-plus"
import { Tickets, View } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const type = ref('我发出的')
const detailDialog = ref(false)
const currentApplication = ref(null)

const filterType = computed(() => (type.value === '我发出的' ? 'sent' : 'received'))

const load = () => {
  request.get("/application/my/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: type.value
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.records || []
      total.value = res.data?.total || 0
    }
  })
}

const viewDetail = (row) => {
  currentApplication.value = row
  detailDialog.value = true
}

const pets = ref([])
const loadPet = () => {
  request.get('/pet').then(res => {
    pets.value = res.data
  })
}

const users = ref([])
const loadUsers = () => {
  request.get('/user').then(res => {
    users.value = res.data
  })
}

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
  loadPet()
  loadUsers()
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>我的领养申请</span>
        </div>
        <div class="head-row">
          <div>
            <h1>我的领养申请</h1>
            <p class="head-desc">查看你发出的申请与收到的申请，跟踪审核进度，及时处理。</p>
          </div>
        </div>
      </div>

      <div class="content-card table-card">
        <div class="table-head">
          <div class="type-tabs">
            <button
              class="type-tab"
              :class="{ active: type === '我发出的' }"
              @click="type = '我发出的'; load()"
            >
              我发出的
            </button>
            <button
              class="type-tab"
              :class="{ active: type === '我收到的' }"
              @click="type = '我收到的'; load()"
            >
              我收到的
            </button>
          </div>
          <span class="table-total">共 {{ total }} 条</span>
        </div>

        <el-table :data="tableData" class="app-table">
          <el-table-column prop="id" label="申请ID" width="90" />
          <el-table-column label="领养宠物" width="140">
            <template #default="scope">
              <span v-if="scope.row.petId">{{ pets.find(item => item.id === scope.row.petId)?.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="申请人" width="110" />
          <el-table-column prop="phone" label="联系电话" width="130" />
          <el-table-column prop="age" label="年龄" width="70" />
          <el-table-column label="经济情况">
            <template #default="scope">
              <el-tag effect="light">{{ scope.row.economy }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请状态" width="120">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === '已通过' ? 'success' : scope.row.status === '已拒绝' ? 'danger' : 'warning'"
                effect="light"
              >
                {{ scope.row.status || '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" plain :icon="View" @click="viewDetail(scope.row)">查看详情</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialog" title="领养申请详情" width="640px" :close-on-click-modal="false">
      <div class="application-detail" v-if="currentApplication">
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <label>申请ID</label>
              <span>{{ currentApplication.id }}</span>
            </div>
            <div class="detail-item">
              <label>领养宠物</label>
              <span>{{ pets.find(item => item.id === currentApplication.petId)?.name }}</span>
            </div>
            <div class="detail-item">
              <label>申请状态</label>
              <el-tag
                :type="currentApplication.status === '已通过' ? 'success' : currentApplication.status === '已拒绝' ? 'danger' : 'warning'"
                effect="light"
              >
                {{ currentApplication.status || '待审核' }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">个人信息</h3>
          <div class="detail-grid">
            <div class="detail-item"><label>姓名</label><span>{{ currentApplication.name }}</span></div>
            <div class="detail-item"><label>年龄</label><span>{{ currentApplication.age }}</span></div>
            <div class="detail-item"><label>性别</label><span>{{ currentApplication.sex }}</span></div>
            <div class="detail-item"><label>联系电话</label><span>{{ currentApplication.phone }}</span></div>
            <div class="detail-item"><label>微信号</label><span>{{ currentApplication.wechat }}</span></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">居住情况</h3>
          <div class="detail-grid">
            <div class="detail-item"><label>居住地址</label><span>{{ currentApplication.address }}</span></div>
            <div class="detail-item"><label>具体地址</label><span>{{ currentApplication.place }}</span></div>
            <div class="detail-item"><label>住房类型</label><span>{{ currentApplication.house }}</span></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">家庭情况</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <label>经济情况</label>
              <el-tag effect="light">{{ currentApplication.economy }}</el-tag>
            </div>
            <div class="detail-item"><label>现有宠物</label><span>{{ currentApplication.animal || '无' }}</span></div>
            <div class="detail-item full"><label>家庭成员</label><span>{{ currentApplication.family }}</span></div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">补充说明</h3>
          <p class="instruction-text">{{ currentApplication.instruction || '无' }}</p>
        </div>

        <div class="detail-section" v-if="filterType === 'sent'">
          <h3 class="section-title">发布方信息</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <label>发布方</label>
              <el-tag effect="light">{{ currentApplication.category }}</el-tag>
            </div>
            <div class="detail-item">
              <label>发布用户</label>
              <span>{{ users.find(item => item.id === currentApplication.fromId)?.nickname }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialog = false">关闭</el-button>
          <el-button
            type="success"
            @click="ok(currentApplication.id); detailDialog = false"
            v-if="currentApplication?.status === '待审核' && filterType === 'received'"
          >
            通过申请
          </el-button>
          <el-button
            type="danger"
            @click="no(currentApplication.id); detailDialog = false"
            v-if="currentApplication?.status === '待审核' && filterType === 'received'"
          >
            拒绝申请
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.table-card {
  padding: 20px;
}

.table-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.type-tabs {
  display: inline-flex;
  padding: 3px;
  border-radius: 10px;
  background: var(--bg-subtle);

  .type-tab {
    padding: 8px 22px;
    border: none;
    background: transparent;
    border-radius: 8px;
    font-size: 13.5px;
    font-weight: 500;
    color: var(--ink-500);
    cursor: pointer;
    transition: all 0.15s ease;

    &.active {
      background: #fff;
      color: var(--brand-600);
      font-weight: 600;
      box-shadow: var(--shadow-sm);
    }
  }
}

.table-total {
  font-size: 12.5px;
  color: var(--ink-400);
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.application-detail {
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
  gap: 8px;
  font-size: 13px;
  line-height: 1.7;

  &.full {
    grid-column: 1 / -1;
  }

  label {
    color: var(--ink-400);
    flex-shrink: 0;
    min-width: 64px;
  }

  span {
    color: var(--ink-700);
  }
}

.instruction-text {
  margin: 0;
  color: var(--ink-700);
  line-height: 1.7;
  white-space: pre-wrap;
  font-size: 13px;
}
</style>
