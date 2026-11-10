<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const article = ref({})
const id = route.query.id

const load = () => {
  request.get("/article/" + id).then(res => {
    article.value = res.data
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  window.scrollTo(0, 0)
  load()
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container narrow">
      <div class="page-header">
        <div class="breadcrumb">
          <a @click="goBack"><el-icon class="back-icon"><ArrowLeft /></el-icon> 返回</a>
          <span class="sep">/</span>
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <router-link to="/front/article">宠物百科</router-link>
        </div>
      </div>

      <article class="content-card article-main">
        <div class="article-head">
          <span class="article-badge">养宠知识</span>
          <h1 class="article-title">{{ article.name }}</h1>
          <p class="article-meta">发布于 {{ article.time }}</p>
        </div>
        <div v-rich-img class="rich-content" v-html="article.content"></div>
      </article>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.page-container.narrow {
  max-width: 860px;
}

.back-icon {
  vertical-align: -2px;
  margin-right: 2px;
}

.article-main {
  overflow: hidden;
}

.article-head {
  padding: 32px 40px 20px;
  border-bottom: 1px solid var(--line-light);
}

.article-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-pill);
  background: var(--teal-50);
  color: var(--teal-600);
  font-size: 12.5px;
  font-weight: 600;
  margin-bottom: 14px;
}

.article-title {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--ink-900);
  letter-spacing: -0.01em;
}

.article-meta {
  margin: 0;
  font-size: 13px;
  color: var(--ink-400);
}

.rich-content {
  padding: 28px 40px 48px;
}
</style>
