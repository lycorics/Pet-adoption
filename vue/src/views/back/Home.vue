<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import * as echarts from 'echarts'
import { Present, Guide, Tickets, User, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

const petCount = ref(0)
const loadPetCount = () => {
  request.get('/pet').then(res => {
    petCount.value = res.data.filter(item => item.category === '官方' && item.status === '待领养').length
  })
}
loadPetCount()

const rescueCount = ref(0)
const loadRescueCount = () => {
  request.get('/rescue').then(res => {
    rescueCount.value = res.data.filter(item => item.status === '待救助').length
  })
}
loadRescueCount()

const applicationCount = ref(0)
const loadApplicationCount = () => {
  request.get('/application').then(res => {
    applicationCount.value = res.data.filter(item => item.status === '待审核').length
  })
}
loadApplicationCount()

const userCount = ref(0)
const loadUserCount = () => {
  request.get('/user').then(res => {
    userCount.value = res.data.length
  })
}
loadUserCount()

const pendingApplications = ref([])
const loadPendingApplications = () => {
  request.get('/application').then(res => {
    pendingApplications.value = res.data.filter(item => item.status === '待审核').slice(0, 5)
  })
}
loadPendingApplications()

const petsMap = ref({})
const loadPetsMap = () => {
  request.get('/pet').then(res => {
    res.data.forEach(p => { petsMap.value[p.id] = p.name })
  })
}
loadPetsMap()

const myChart = ref(null)
let chartInstance = null

onMounted(() => {
  chartInstance = echarts.init(document.getElementById('main'))
  const option = {
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: { color: '#6b7280', fontSize: 12 }
    },
    series: [
      {
        name: '宠物数量',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '44%'],
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          color: '#3d4148',
          fontSize: 12,
          lineHeight: 17,
          formatter: '{b}\n{c} 只'
        },
        labelLine: {
          length: 16,
          length2: 10,
          lineStyle: { color: '#c6c9cf' }
        },
        color: ['#e8613c', '#2e9e8f', '#d98324', '#4f7fd9', '#9a6fd9', '#d94a4a', '#5aa9e6', '#8a9b6e'],
        data: []
      }
    ]
  }

  request.get("/echarts/count").then(res => {
    // 过滤数量为 0 的品种：零值扇形宽度为 0，标签会全部扎堆指向同一处
    option.series[0].data = (res.data || []).filter(item => Number(item.value) > 0)
    chartInstance.setOption(option)
  })
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<template>
  <div class="dashboard">
    <!-- 页头 -->
    <div class="dash-head">
      <div>
        <h1>数据总览</h1>
        <p>欢迎回来，这里是今日平台运营概览。</p>
      </div>
      <span class="dash-date">{{ new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }) }}</span>
    </div>

    <!-- KPI -->
    <div class="kpi-grid">
      <div class="kpi-card" @click="router.push('/back/application')">
        <div class="kpi-icon warn"><el-icon><Tickets /></el-icon></div>
        <div class="kpi-body">
          <div class="kpi-num">{{ applicationCount }}</div>
          <div class="kpi-label">待审核领养申请</div>
        </div>
        <el-icon class="kpi-arrow"><ArrowRight /></el-icon>
      </div>

      <div class="kpi-card" @click="router.push('/back/pet')">
        <div class="kpi-icon brand"><el-icon><Present /></el-icon></div>
        <div class="kpi-body">
          <div class="kpi-num">{{ petCount }}</div>
          <div class="kpi-label">官方可领养宠物</div>
        </div>
        <el-icon class="kpi-arrow"><ArrowRight /></el-icon>
      </div>

      <div class="kpi-card" @click="router.push('/back/rescue')">
        <div class="kpi-icon danger"><el-icon><Guide /></el-icon></div>
        <div class="kpi-body">
          <div class="kpi-num">{{ rescueCount }}</div>
          <div class="kpi-label">待救助事件</div>
        </div>
        <el-icon class="kpi-arrow"><ArrowRight /></el-icon>
      </div>

      <div class="kpi-card" @click="router.push('/back/user')">
        <div class="kpi-icon teal"><el-icon><User /></el-icon></div>
        <div class="kpi-body">
          <div class="kpi-num">{{ userCount }}</div>
          <div class="kpi-label">注册用户</div>
        </div>
        <el-icon class="kpi-arrow"><ArrowRight /></el-icon>
      </div>
    </div>

    <div class="dash-grid">
      <!-- 宠物种类分布 -->
      <div class="content-card chart-card">
        <div class="card-head">
          <h3>宠物种类分布</h3>
          <span class="card-sub">全站宠物数据</span>
        </div>
        <div id="main" class="chart-main"></div>
      </div>

      <!-- 待办 + 快捷入口 -->
      <div class="side-stack">
        <div class="content-card todo-card">
          <div class="card-head">
            <h3>待审核申请</h3>
            <span class="card-link" @click="router.push('/back/application')">查看全部</span>
          </div>

          <div v-if="pendingApplications.length === 0" class="todo-empty">
            暂无待审核申请
          </div>

          <div v-else class="todo-list">
            <div
              v-for="app in pendingApplications"
              :key="app.id"
              class="todo-item"
              @click="router.push('/back/application')"
            >
              <div class="todo-avatar">
                {{ (app.name || '?').slice(0, 1) }}
              </div>
              <div class="todo-body">
                <div class="todo-title">
                  {{ app.name }} 申请领养
                  <strong>{{ petsMap[app.petId] || '未知宠物' }}</strong>
                </div>
                <div class="todo-meta">
                  申请ID {{ app.id }} · {{ app.phone }}
                </div>
              </div>
              <el-tag type="warning" effect="light" size="small">待审核</el-tag>
            </div>
          </div>
        </div>

        <div class="content-card quick-card">
          <div class="card-head">
            <h3>快捷操作</h3>
          </div>
          <div class="quick-grid">
            <div class="quick-item" @click="router.push('/back/pet')">
              <el-icon><Present /></el-icon>
              <span>发布宠物</span>
            </div>
            <div class="quick-item" @click="router.push('/back/trend')">
              <el-icon><Guide /></el-icon>
              <span>发布动态</span>
            </div>
            <div class="quick-item" @click="router.push('/back/article')">
              <el-icon><User /></el-icon>
              <span>写百科</span>
            </div>
            <div class="quick-item" @click="router.push('/back/type')">
              <el-icon><Present /></el-icon>
              <span>管理种类</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.dashboard {
  width: 90%;
  max-width: 1400px;
  margin: 0 auto;
}

.dash-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;

  h1 {
    margin: 0 0 6px;
    font-size: 22px;
    font-weight: 700;
    color: var(--ink-900);
  }

  p {
    margin: 0;
    font-size: 13.5px;
    color: var(--ink-500);
  }

  .dash-date {
    font-size: 13px;
    color: var(--ink-400);
  }
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.kpi-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.kpi-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 21px;
  flex-shrink: 0;

  &.brand {
    background: var(--brand-50);
    color: var(--brand-600);
  }

  &.warn {
    background: var(--warn-50);
    color: var(--warn-500);
  }

  &.danger {
    background: var(--danger-50);
    color: var(--danger-500);
  }

  &.teal {
    background: var(--teal-50);
    color: var(--teal-600);
  }
}

.kpi-body {
  flex: 1;

  .kpi-num {
    font-size: 26px;
    font-weight: 700;
    color: var(--ink-900);
    line-height: 1.2;
  }

  .kpi-label {
    margin-top: 2px;
    font-size: 12.5px;
    color: var(--ink-500);
  }
}

.kpi-arrow {
  color: var(--ink-300);
  font-size: 14px;
}

.dash-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
  align-items: start;
}

.chart-card,
.todo-card,
.quick-card {
  padding: 20px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: var(--ink-900);
  }

  .card-sub {
    font-size: 12px;
    color: var(--ink-400);
  }

  .card-link {
    font-size: 12.5px;
    color: var(--brand-600);
    cursor: pointer;
  }
}

.chart-main {
  width: 100%;
  height: 320px;
}

.side-stack {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-empty {
  padding: 40px 0;
  text-align: center;
  font-size: 13px;
  color: var(--ink-400);
}

.todo-list {
  display: flex;
  flex-direction: column;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 0;
  border-bottom: 1px solid var(--line-light);
  cursor: pointer;
  transition: background 0.15s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--bg-subtle);
  }
}

.todo-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--brand-50);
  color: var(--brand-600);
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.todo-body {
  flex: 1;
  min-width: 0;

  .todo-title {
    font-size: 13.5px;
    color: var(--ink-700);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    strong {
      color: var(--ink-900);
    }
  }

  .todo-meta {
    margin-top: 2px;
    font-size: 12px;
    color: var(--ink-400);
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 6px;
  border-radius: var(--radius-sm);
  background: var(--bg-subtle);
  cursor: pointer;
  transition: all 0.15s ease;

  .el-icon {
    font-size: 20px;
    color: var(--brand-600);
  }

  span {
    font-size: 12px;
    color: var(--ink-500);
  }

  &:hover {
    background: var(--brand-50);

    span {
      color: var(--brand-600);
    }
  }
}

@media (max-width: 1100px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dash-grid {
    grid-template-columns: 1fr;
  }
}
</style>
