<script setup>
import { onMounted, ref, computed } from 'vue'
import request from '@/utils/request.js'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Location, Present, CircleCheck } from '@element-plus/icons-vue'

const account = ref(localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {})

const route = useRoute()
const router = useRouter()

const pet = ref({})
const id = route.query.id

const types = ref([])
const healths = ref([])

const imageList = computed(() => {
  if (!pet.value.imgList) return []
  return pet.value.imgList.split(',').filter(img => img.trim())
})

const activeImg = ref(0)

const load = () => {
  request.get("/pet/" + id).then(res => {
    pet.value = res.data
    pet.value.imgList = pet.value.img + ',' + (pet.value.imgList || '')
  })
}

const goBack = () => {
  router.back()
}

const loadType = () => {
  request.get('/type').then(res => {
    types.value = res.data
  })
}

const loadHealth = () => {
  request.get('/health').then(res => {
    healths.value = res.data
  })
}

const getHealthNames = (idsString) => {
  if (!idsString) return ''
  const idArray = idsString.split(',')
  const array = idArray.map(id => {
    const item = healths.value.find(item => item.id === id || item.id === Number(id))
    return item ? item.name : ''
  })
  return array.join(', ')
}

const typeName = computed(() => types.value.find(item => item.id === pet.value.typeId)?.name || '未知品种')

onMounted(() => {
  load()
  loadType()
  loadHealth()
  window.scrollTo(0, 0)
})
</script>

<template>
  <div class="page-shell pet-detail-page">
    <div class="page-container">
      <!-- 面包屑 -->
      <div class="page-header">
        <div class="breadcrumb">
          <a @click="goBack"><el-icon class="back-icon"><ArrowLeft /></el-icon> 返回列表</a>
          <span class="sep">/</span>
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <router-link to="/front/pet">领养中心</router-link>
          <span class="sep">/</span>
          <span>{{ pet.name }}</span>
        </div>
      </div>

      <div class="detail-layout">
        <!-- 左：图片 -->
        <div class="gallery-col">
          <div class="gallery-main">
            <img
              v-if="imageList.length"
              :src="imageList[activeImg]"
              :alt="pet.name"
              class="gallery-img"
            />
            <div v-else class="gallery-empty">
              <el-icon class="gallery-empty-icon"><Present /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>
          <div class="gallery-thumbs" v-if="imageList.length > 1">
            <div
              v-for="(img, index) in imageList"
              :key="index"
              class="thumb"
              :class="{ active: activeImg === index }"
              @click="activeImg = index"
            >
              <img :src="img" :alt="pet.name" />
            </div>
          </div>
        </div>

        <!-- 右：信息卡 -->
        <div class="info-col">
          <div class="content-card info-card">
            <div class="card-head">
              <span class="cat-badge">{{ pet.category || '个人发布' }}</span>
              <span class="chip-id">编号 #{{ pet.id }}</span>
            </div>

            <h1 class="pet-name">{{ pet.name }}</h1>
            <p class="pet-greeting">
              你好，我是一只{{ typeName }}，正在等待一个温暖的家。
            </p>

            <div class="info-grid">
              <div class="info-cell">
                <span class="info-label">性别</span>
                <span class="info-value">{{ pet.sex || '未知' }}</span>
              </div>
              <div class="info-cell">
                <span class="info-label">年龄</span>
                <span class="info-value">{{ pet.age || '未知' }}</span>
              </div>
              <div class="info-cell">
                <span class="info-label">品种</span>
                <span class="info-value">{{ typeName }}</span>
              </div>
              <div class="info-cell">
                <span class="info-label">所在地区</span>
                <span class="info-value">
                  <el-icon class="v-icon"><Location /></el-icon>
                  {{ pet.address || '待确认' }}
                </span>
              </div>
            </div>

            <div class="health-block" v-if="getHealthNames(pet.health)">
              <span class="health-title">健康状况</span>
              <div class="health-tags">
                <span
                  v-for="h in getHealthNames(pet.health).split(', ')"
                  :key="h"
                  class="health-tag"
                >
                  <el-icon><CircleCheck /></el-icon>
                  {{ h }}
                </span>
              </div>
            </div>

            <div class="card-actions">
              <el-button
                v-if="pet.userId !== account.id"
                type="primary"
                size="large"
                class="adopt-btn"
                @click="router.push('/front/application?id=' + pet.id)"
              >
                <el-icon class="btn-icon"><Present /></el-icon>
                带我回家
              </el-button>
              <div v-else class="own-note">这是你发布的宠物</div>
              <p class="action-tip" v-if="pet.userId !== account.id">
                提交领养申请后，我们会尽快审核并与你联系
              </p>
            </div>

            <p class="publish-time" v-if="pet.time">发布于 {{ pet.time }}</p>
          </div>
        </div>
      </div>

      <!-- 关于我 -->
      <div class="content-card about-card" v-if="pet.content">
        <div class="about-head">
          <h2>关于我</h2>
        </div>
        <div v-rich-img class="rich-content" v-html="pet.content"></div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.pet-detail-page {
  .page-header {
    padding-bottom: 16px;
  }

  .back-icon {
    margin-right: 2px;
    vertical-align: -2px;
  }
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(340px, 0.8fr);
  gap: 24px;
  align-items: start;
}

/* 图片 */
.gallery-col {
  position: sticky;
  top: calc(var(--nav-h) + 20px);
}

.gallery-main {
  height: 460px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: center;

  .gallery-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .gallery-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    color: var(--ink-400);
    font-size: 14px;

    .gallery-empty-icon {
      font-size: 40px;
      color: var(--ink-300);
    }
  }
}

.gallery-thumbs {
  display: flex;
  gap: 10px;
  margin-top: 12px;

  .thumb {
    width: 76px;
    height: 60px;
    border-radius: var(--radius-sm);
    overflow: hidden;
    border: 2px solid var(--line);
    cursor: pointer;
    transition: border-color 0.15s ease;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &.active {
      border-color: var(--brand-500);
    }
  }
}

/* 信息卡 */
.info-card {
  padding: 24px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;

  .cat-badge {
    padding: 4px 12px;
    border-radius: var(--radius-pill);
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 12.5px;
    font-weight: 600;
  }

  .chip-id {
    font-size: 12.5px;
    color: var(--ink-400);
  }
}

.pet-name {
  margin: 0 0 8px;
  font-size: 30px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.01em;
}

.pet-greeting {
  margin: 0 0 20px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--ink-500);
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  padding: 16px 0;
  border-top: 1px solid var(--line-light);
  border-bottom: 1px solid var(--line-light);
}

.info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  background: var(--bg-subtle);
  border-radius: var(--radius-sm);

  .info-label {
    font-size: 12px;
    color: var(--ink-400);
  }

  .info-value {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 14.5px;
    font-weight: 500;
    color: var(--ink-900);
  }

  .v-icon {
    color: var(--ink-400);
  }
}

.health-block {
  padding: 16px 0;
  border-bottom: 1px solid var(--line-light);

  .health-title {
    display: block;
    font-size: 13px;
    color: var(--ink-500);
    margin-bottom: 10px;
  }

  .health-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .health-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 5px 12px;
    border-radius: var(--radius-pill);
    background: var(--teal-50);
    color: var(--teal-600);
    font-size: 12.5px;
    font-weight: 500;
  }
}

.card-actions {
  padding: 20px 0 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;

  .adopt-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
  }

  .btn-icon {
    margin-right: 4px;
  }

  .action-tip {
    margin: 0;
    font-size: 12.5px;
    color: var(--ink-400);
  }

  .own-note {
    padding: 12px 20px;
    border-radius: 10px;
    background: var(--brand-50);
    color: var(--brand-600);
    font-size: 14px;
    font-weight: 500;
  }
}

.publish-time {
  margin: 16px 0 0;
  padding-top: 14px;
  border-top: 1px solid var(--line-light);
  font-size: 12.5px;
  color: var(--ink-400);
  text-align: center;
}

/* 关于我 */
.about-card {
  margin-top: 24px;
  padding: 28px;

  .about-head {
    margin-bottom: 16px;

    h2 {
      margin: 0;
      font-size: 19px;
      font-weight: 700;
      color: var(--ink-900);
    }
  }
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .gallery-col {
    position: static;
  }
}
</style>
