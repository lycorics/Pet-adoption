<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRouter } from 'vue-router'
import { Search, ArrowRight } from "@element-plus/icons-vue";

const router = useRouter()
const types = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(8)
const keyword = ref('')

const load = () => {
  request.get('/type/page', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
    }
  }).then(res => {
    if (res.code === '200') {
      types.value = res.data.records || []
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
          <span>种类图鉴</span>
        </div>
        <div class="head-row">
          <div>
            <h1>宠物种类图鉴</h1>
            <p class="head-desc">了解不同品种的体型、寿命、性格特点，找到最适合你的家庭成员。</p>
          </div>
        </div>
      </div>

      <div class="content-card search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索宠物种类…"
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

      <div v-if="types.length === 0" class="content-card empty-block">
        <div class="empty-icon"><Search /></div>
        <p>暂无相关种类</p>
      </div>

      <div v-else class="types-grid">
        <div
          v-for="type in types"
          :key="type.id"
          class="type-card"
          @click="router.push('/front/typeDetail?id=' + type.id)"
        >
          <div class="type-media">
            <img :src="type.img" :alt="type.name" />
          </div>
          <div class="type-body">
            <h3 class="type-name">{{ type.name }}</h3>
            <div class="type-tags">
              <span v-if="type.body">{{ type.body }}</span>
              <span v-if="type.hair">{{ type.hair }}</span>
              <span v-if="type.lifespan">{{ type.lifespan }}</span>
            </div>
            <p class="type-info">{{ type.info }}</p>
            <span class="type-link">
              查看图鉴 <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[4, 8, 12, 16]"
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

.types-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.type-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-3px);

    .type-media img {
      transform: scale(1.04);
    }
  }
}

.type-media {
  height: 200px;
  overflow: hidden;
  background: var(--bg-subtle);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }
}

.type-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.type-name {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight: 600;
  color: var(--ink-900);
}

.type-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;

  span {
    padding: 2px 8px;
    border-radius: var(--radius-sm);
    background: var(--bg-subtle);
    font-size: 12px;
    color: var(--ink-500);
  }
}

.type-info {
  margin: 0 0 12px;
  font-size: 12.5px;
  line-height: 1.6;
  color: var(--ink-500);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.type-link {
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

@media (max-width: 1024px) {
  .types-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .types-grid {
    grid-template-columns: 1fr;
  }
}
</style>
