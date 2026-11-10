<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRouter } from 'vue-router'
import { Search, ArrowRight } from "@element-plus/icons-vue";

const router = useRouter()
const trends = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(6)
const keyword = ref('')

const load = () => {
  request.get('/trend/page', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
    }
  }).then(res => {
    if (res.code === '200') {
      trends.value = res.data.records || []
      total.value = res.data?.total || 0
    }
  })
}

const reset = () => {
  keyword.value = ""
  load()
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
  window.scrollTo(0, 0)
  load()
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>团队动态</span>
        </div>
        <div class="head-row">
          <div>
            <h1>团队动态</h1>
            <p class="head-desc">记录每一次救助与领养背后的故事，见证爱与行动的发生。</p>
          </div>
        </div>
      </div>

      <!-- 搜索 -->
      <div class="content-card search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索团队动态…"
          class="search-input"
          size="large"
          clearable
          @keyup.enter="load"
          @clear="load"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" @click="load">搜索</el-button>
        <el-button size="large" @click="reset">重置</el-button>
      </div>

      <!-- 列表 -->
      <div v-if="trends.length === 0" class="content-card empty-block">
        <div class="empty-icon"><Search /></div>
        <p>暂无相关动态</p>
      </div>

      <div v-else class="trends-grid">
        <div
          v-for="trend in trends"
          :key="trend.id"
          class="trend-card"
          @click="router.push('/front/trendDetail?id=' + trend.id)"
        >
          <div class="trend-media">
            <img :src="trend.img" :alt="trend.name" />
          </div>
          <div class="trend-body">
            <span class="trend-badge">团队动态</span>
            <h3 class="trend-name">{{ trend.name }}</h3>
            <p class="trend-time">{{ trend.time }}</p>
            <span class="trend-link">
              了解详情 <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[6, 10, 16, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total"
          background
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  margin-bottom: 24px;

  .search-input {
    max-width: 460px;
  }
}

.trends-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.trend-card {
  display: flex;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-3px);

    .trend-media img {
      transform: scale(1.04);
    }
  }
}

.trend-media {
  width: 240px;
  flex-shrink: 0;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }
}

.trend-body {
  flex: 1;
  min-width: 0;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
}

.trend-badge {
  align-self: flex-start;
  padding: 3px 10px;
  border-radius: var(--radius-pill);
  background: var(--brand-50);
  color: var(--brand-600);
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 10px;
}

.trend-name {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 600;
  color: var(--ink-900);
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.trend-time {
  margin: 0 0 14px;
  font-size: 13px;
  color: var(--ink-400);
}

.trend-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: auto;
  font-size: 13px;
  font-weight: 500;
  color: var(--brand-600);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 8px;
}

@media (max-width: 900px) {
  .trends-grid {
    grid-template-columns: 1fr;
  }

  .trend-card {
    flex-direction: column;
  }

  .trend-media {
    width: 100%;
    height: 200px;
  }
}
</style>
