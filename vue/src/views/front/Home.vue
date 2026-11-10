<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRouter } from "vue-router";
import { Present, Guide, Reading, ArrowRight, TrendCharts } from '@element-plus/icons-vue'
import { useTransition } from '@vueuse/core'

const router = useRouter()

const trends = ref([])
const loadTrend = () => {
  request.get("/trend").then(res => {
    trends.value = res.data.splice(0, 3)
  })
}

const articles = ref([])
const loadArticle = () => {
  request.get("/article").then(res => {
    articles.value = res.data.splice(0, 4)
  })
}

const pets = ref([])
const availablePets = ref([])
const petCount = ref(0)
const petValue = useTransition(petCount, { duration: 500 })

const loadPet = () => {
  request.get('/pet').then(res => {
    pets.value = res.data.filter(item => item.status === '已领养')
    petCount.value = pets.value.length
    availablePets.value = res.data.filter(item => item.status === '待领养').slice(0, 4)
  })
}

const rescues = ref([])
const rescueCount = ref(0)
const rescueValue = useTransition(rescueCount, { duration: 500 })

const loadRescue = () => {
  request.get('/rescue').then(res => {
    rescues.value = res.data.filter(item => item.status === '已救助')
    rescueCount.value = rescues.value.length
  })
}

onMounted(() => {
  loadTrend()
  loadArticle()
  loadPet()
  loadRescue()
})
</script>

<template>
  <div class="home-page">
    <!-- 首屏：价值主张 -->
    <section class="hero-band">
      <div class="hero-inner">
        <div class="hero-text">
          <span class="hero-badge">公益领养平台</span>
          <h1>以爱为名<br />给流浪的它们一个家</h1>
          <p>
            我们相信每一个生命都值得被善待。在这里，遇见待领养的毛孩子，
            参与紧急救助，学习科学养宠知识。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" round @click="router.push('/front/pet')">
              <el-icon class="btn-icon"><Present /></el-icon>
              去看看毛孩子
            </el-button>
            <el-button size="large" round @click="router.push('/front/rescue')">
              <el-icon class="btn-icon"><Guide /></el-icon>
              我要救助
            </el-button>
          </div>
        </div>
        <div class="hero-visual">
          <img src="../../assets/领养宠物.png" alt="领养宠物" class="hero-img" />
        </div>
      </div>
    </section>

    <!-- 数据条 -->
    <section class="stats-band">
      <div class="page-container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-num"><el-statistic :value="availablePets.length" /></div>
            <div class="stat-label">正在等待领养</div>
          </div>
          <div class="stat-item">
            <div class="stat-num"><el-statistic :value="petValue" /></div>
            <div class="stat-label">已成功领养</div>
          </div>
          <div class="stat-item">
            <div class="stat-num"><el-statistic :value="rescueValue" /></div>
            <div class="stat-label">已完成救助</div>
          </div>
          <div class="stat-item">
            <div class="stat-num"><el-statistic :value="articles.length" /></div>
            <div class="stat-label">养宠百科内容</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 待领养宠物（核心） -->
    <section class="section-block">
      <div class="page-container">
        <div class="section-head">
          <div class="section-title">
            <el-icon class="title-icon"><Present /></el-icon>
            <span>等待回家的毛孩子</span>
          </div>
          <span class="section-link" @click="router.push('/front/pet')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </span>
        </div>

        <div v-if="availablePets.length" class="pet-grid">
          <div
            v-for="pet in availablePets"
            :key="pet.id"
            class="pet-card"
            @click="router.push('/front/petDetail?id=' + pet.id)"
          >
            <div class="pet-media">
              <img :src="pet.img" :alt="pet.name" class="pet-img" />
              <span class="pet-cat">{{ pet.category }}</span>
            </div>
            <div class="pet-body">
              <div class="pet-head">
                <h3 class="pet-name">{{ pet.name }}</h3>
                <span class="pet-sex" :class="pet.sex === '公' ? 'male' : 'female'">{{ pet.sex }}</span>
              </div>
              <p class="pet-meta">
                {{ pet.age }} · {{ pet.address || '位置待确认' }}
              </p>
              <div class="pet-foot">
                <span class="pet-address">{{ pet.address || '位置待确认' }}</span>
                <span class="pet-cta">了解详情 <el-icon><ArrowRight /></el-icon></span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-block">
          <div class="empty-icon"><Present /></div>
          <p>暂时没有待领养的毛孩子，敬请期待</p>
        </div>
      </div>
    </section>

    <!-- 救助与团队动态 -->
    <section class="section-block alt">
      <div class="page-container">
        <div class="section-head">
          <div class="section-title">
            <el-icon class="title-icon"><TrendCharts /></el-icon>
            <span>我们在行动</span>
          </div>
          <span class="section-link" @click="router.push('/front/trend')">
            全部动态 <el-icon><ArrowRight /></el-icon>
          </span>
        </div>

        <div class="trend-grid">
          <div
            v-for="trend in trends"
            :key="trend.id"
            class="trend-card"
            @click="router.push('/front/trendDetail?id=' + trend.id)"
          >
            <img :src="trend.img" :alt="trend.name" class="trend-img" />
            <div class="trend-body">
              <h3>{{ trend.name }}</h3>
              <p>{{ trend.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 宠物百科 -->
    <section class="section-block">
      <div class="page-container">
        <div class="section-head">
          <div class="section-title">
            <el-icon class="title-icon"><Reading /></el-icon>
            <span>科学养宠百科</span>
          </div>
          <span class="section-link" @click="router.push('/front/article')">
            全部文章 <el-icon><ArrowRight /></el-icon>
          </span>
        </div>

        <div class="article-grid">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="router.push('/front/articleDetail?id=' + article.id)"
          >
            <img :src="article.img" :alt="article.name" class="article-img" />
            <div class="article-body">
              <h3>{{ article.name }}</h3>
              <span>{{ article.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
}

/* 首屏 */
.hero-band {
  background: linear-gradient(160deg, #fdf3ee 0%, #fbe9df 60%, #fdf1ec 100%);
  border-bottom: 1px solid var(--line-light);
  overflow: hidden;
}

.hero-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 64px 24px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 48px;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 5px 14px;
  border-radius: var(--radius-pill);
  background: #fff;
  color: var(--brand-600);
  font-size: 12.5px;
  font-weight: 600;
  box-shadow: var(--shadow-sm);
  margin-bottom: 20px;
}

.hero-text {
  h1 {
    font-size: 40px;
    line-height: 1.28;
    font-weight: 700;
    color: var(--ink-900);
    margin: 0 0 18px;
    letter-spacing: -0.01em;
  }

  p {
    font-size: 15.5px;
    line-height: 1.8;
    color: var(--ink-500);
    margin: 0 0 30px;
    max-width: 480px;
  }
}

.hero-actions {
  display: flex;
  gap: 12px;

  .btn-icon {
    margin-right: 2px;
  }
}

.hero-visual {
  display: flex;
  justify-content: center;

  .hero-img {
    width: 100%;
    max-width: 480px;
    max-height: 360px;
    object-fit: cover;
    border-radius: 18px;
    box-shadow: var(--shadow-lg);
    border: 6px solid #fff;
  }
}

/* 数据条 */
.stats-band {
  background: #fff;
  border-bottom: 1px solid var(--line);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 28px 0;
}

.stat-item {
  text-align: center;
  border-right: 1px solid var(--line-light);
  padding: 8px 0;

  &:last-child {
    border-right: none;
  }

  .stat-num {
    font-size: 28px;
    font-weight: 700;
    color: var(--brand-600);
    line-height: 1.2;
  }

  .stat-num :deep(.el-statistic__content) {
    font-size: 28px;
    font-weight: 700;
    color: var(--brand-600);
  }

  .stat-label {
    margin-top: 6px;
    font-size: 13px;
    color: var(--ink-500);
  }
}

/* 区块 */
.section-block {
  padding: 48px 0;
  background: var(--bg-page);

  &.alt {
    background: #fff;
    border-top: 1px solid var(--line);
    border-bottom: 1px solid var(--line);
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 21px;
  font-weight: 700;
  color: var(--ink-900);

  .title-icon {
    width: 38px;
    height: 38px;
    border-radius: 10px;
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 19px;
  }
}

.section-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13.5px;
  color: var(--ink-500);
  cursor: pointer;
  transition: color 0.15s ease;

  &:hover {
    color: var(--brand-600);
  }
}

/* 宠物卡片 */
.pet-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 24px;
}

.pet-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-3px);

    .pet-img {
      transform: scale(1.04);
    }
  }
}

.pet-media {
  position: relative;
  height: 190px;
  overflow: hidden;
  background: var(--bg-subtle);

  .pet-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }

  .pet-cat {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 12px;
    border-radius: var(--radius-pill);
    background: rgba(255, 255, 255, 0.92);
    color: var(--ink-700);
    font-size: 12px;
    font-weight: 600;
    backdrop-filter: blur(4px);
  }
}

.pet-body {
  padding: 14px 16px 16px;
}

.pet-head {
  display: flex;
  align-items: center;
  gap: 8px;

  .pet-name {
    margin: 0;
    font-size: 17px;
    font-weight: 600;
    color: var(--ink-900);
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .pet-sex {
    font-size: 12px;
    font-weight: 600;
    padding: 2px 8px;
    border-radius: var(--radius-pill);

    &.male {
      background: var(--el-color-primary-light-9);
      color: var(--brand-600);
    }

    &.female {
      background: var(--el-color-danger-light-9);
      color: var(--danger-500);
    }
  }
}

.pet-meta {
  margin: 8px 0 12px;
  font-size: 13px;
  color: var(--ink-500);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pet-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--line-light);

  .pet-address {
    font-size: 12.5px;
    color: var(--ink-400);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .pet-cta {
    display: inline-flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    font-weight: 500;
    color: var(--brand-600);
    flex-shrink: 0;
  }
}

/* 动态卡片 */
.trend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 24px;
}

.trend-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-3px);
  }

  .trend-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
    display: block;
  }

  .trend-body {
    padding: 14px 16px;

    h3 {
      margin: 0 0 6px;
      font-size: 15.5px;
      font-weight: 600;
      color: var(--ink-900);
      line-height: 1.45;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    p {
      margin: 0;
      font-size: 12.5px;
      color: var(--ink-400);
    }
  }
}

/* 百科卡片 */
.article-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 24px;
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
  }

  .article-img {
    width: 100%;
    height: 140px;
    object-fit: cover;
    display: block;
  }

  .article-body {
    padding: 14px 16px;

    h3 {
      margin: 0 0 6px;
      font-size: 15px;
      font-weight: 600;
      color: var(--ink-900);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    span {
      font-size: 12.5px;
      color: var(--ink-400);
    }
  }
}

@media (max-width: 1024px) {
  .pet-grid,
  .article-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .trend-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .hero-inner {
    grid-template-columns: 1fr;
    padding: 40px 20px;
  }

  .hero-visual {
    display: none;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    row-gap: 20px;

    .stat-item:nth-child(2) {
      border-right: none;
    }
  }
}
</style>
