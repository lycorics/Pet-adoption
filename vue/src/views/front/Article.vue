<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(6)
const keyword = ref('')

const load = () => {
  request.get('/article/page', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
    }
  }).then(res => {
    if (res.code === '200') {
      articles.value = res.data.records || []
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
          <span>宠物百科</span>
        </div>
        <div class="head-row">
          <div>
            <h1>宠物百科</h1>
            <p class="head-desc">科学养宠，从了解开始。覆盖品种、喂养、健康与行为训练的实用知识。</p>
          </div>
        </div>
      </div>

      <div class="content-card search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索养宠知识…"
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

      <div v-if="articles.length === 0" class="content-card empty-block">
        <div class="empty-icon"><Search /></div>
        <p>暂无相关文章</p>
      </div>

      <div v-else class="articles-grid">
        <div
          v-for="article in articles"
          :key="article.id"
          class="article-card"
          @click="router.push('/front/articleDetail?id=' + article.id)"
        >
          <div class="article-media">
            <img :src="article.img" :alt="article.name" />
          </div>
          <div class="article-body">
            <span class="article-badge">养宠知识</span>
            <h3 class="article-title">{{ article.name }}</h3>
            <p class="article-time">{{ article.time }}</p>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[6, 12, 15, 30]"
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

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.article-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-3px);

    .article-media img {
      transform: scale(1.04);
    }
  }
}

.article-media {
  height: 170px;
  overflow: hidden;
  background: var(--bg-subtle);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }
}

.article-body {
  padding: 16px;
}

.article-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: var(--radius-pill);
  background: var(--teal-50);
  color: var(--teal-600);
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 10px;
}

.article-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-900);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-time {
  margin: 0;
  font-size: 12.5px;
  color: var(--ink-400);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 8px;
}
</style>
