<script setup>
import { onMounted, ref, computed } from 'vue'
import request from '@/utils/request.js'
import { useRoute } from 'vue-router'

const route = useRoute()

const type = ref({})
const id = route.query.id

const load = () => {
  request.get("/type/" + id).then(res => {
    type.value = res.data
  })
}

onMounted(() => {
  load()
  window.scrollTo(0, 0)
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <router-link to="/front/type">种类图鉴</router-link>
          <span class="sep">/</span>
          <span>{{ type.name }}</span>
        </div>
      </div>

      <div class="detail-layout">
        <div class="photo-col">
          <div class="content-card photo-card">
            <img :src="type.img" :alt="type.name" class="type-image" />
          </div>
        </div>

        <div class="info-col">
          <div class="content-card info-card">
            <div class="info-head">
              <h1>{{ type.name }}的资料</h1>
              <span class="info-badge">品种图鉴</span>
            </div>

            <div class="spec-grid">
              <div class="spec-cell">
                <span class="spec-label">原产地</span>
                <span class="spec-value">{{ type.origin || '未知' }}</span>
              </div>
              <div class="spec-cell">
                <span class="spec-label">体型</span>
                <span class="spec-value">{{ type.body || '未知' }}</span>
              </div>
              <div class="spec-cell">
                <span class="spec-label">寿命</span>
                <span class="spec-value">{{ type.lifespan || '未知' }}</span>
              </div>
              <div class="spec-cell">
                <span class="spec-label">毛长</span>
                <span class="spec-value">{{ type.hair || '未知' }}</span>
              </div>
              <div class="spec-cell">
                <span class="spec-label">身高</span>
                <span class="spec-value">{{ type.height || '未知' }}</span>
              </div>
              <div class="spec-cell">
                <span class="spec-label">可训度</span>
                <el-rate v-model="type.trainability" disabled size="small" />
              </div>
              <div class="spec-cell">
                <span class="spec-label">掉毛度</span>
                <el-rate v-model="type.shed" disabled size="small" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-card detail-block">
        <h2 class="block-title">基本信息</h2>
        <p class="block-text">{{ type.info }}</p>
      </div>

      <div class="content-card detail-block" v-if="type.content">
        <h2 class="block-title">更多详情</h2>
        <div v-rich-img class="rich-content" v-html="type.content"></div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.detail-layout {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 24px;
  align-items: start;
}

.photo-card {
  overflow: hidden;
  padding: 0;

  .type-image {
    width: 100%;
    height: 100%;
    min-height: 320px;
    object-fit: cover;
    display: block;
  }
}

.info-card {
  padding: 28px;
}

.info-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 18px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--line-light);

  h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 700;
    color: var(--ink-900);
  }

  .info-badge {
    padding: 4px 12px;
    border-radius: var(--radius-pill);
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 12.5px;
    font-weight: 600;
  }
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.spec-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
  background: var(--bg-subtle);
  border-radius: var(--radius-sm);

  .spec-label {
    font-size: 12px;
    color: var(--ink-400);
  }

  .spec-value {
    font-size: 15px;
    font-weight: 500;
    color: var(--ink-900);
  }
}

.detail-block {
  margin-top: 24px;
  padding: 28px;

  .block-title {
    margin: 0 0 16px;
    font-size: 18px;
    font-weight: 600;
    color: var(--ink-900);
  }

  .block-text {
    margin: 0;
    font-size: 14.5px;
    line-height: 1.8;
    color: var(--ink-700);
  }
}

@media (max-width: 860px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
