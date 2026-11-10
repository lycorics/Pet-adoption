<script setup>
import { onMounted, ref, computed } from 'vue'
import request from '@/utils/request.js'
import { useRouter } from "vue-router";
import { Search, Present, Location, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const pets = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const keyword = ref('')
const sex = ref('所有')
const address = ref('')
const category = ref('所有')

const load = () => {
  request.get("/pet/front/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      sex: sex.value,
      address: address.value,
      category: category.value
    }
  }).then(res => {
    if (res.data) {
      pets.value = res.data.records || []
      total.value = res.data?.total || 0
    }
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

const types = ref([])
const loadType = () => {
  request.get('/type').then(res => {
    types.value = res.data
  })
}

const typeName = (typeId) => types.value.find(item => item.id === typeId)?.name || ''

onMounted(() => {
  load()
  loadType()
  window.scrollTo(0, 0)
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container">
      <!-- 页头 -->
      <div class="page-header">
        <div class="breadcrumb">
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <span>领养中心</span>
        </div>
        <div class="head-row">
          <div>
            <h1>领养中心</h1>
            <p class="head-desc">每一只等待领养的毛孩子都有自己的故事，找到与你投缘的那一位，给它一个家。</p>
          </div>
          <el-button type="primary" round @click="router.push('/front/rescue')">
            发布救助信息
          </el-button>
        </div>
      </div>

      <!-- 筛选栏 -->
      <div class="content-card filter-bar">
        <div class="filter-group">
          <span class="filter-label">性别</span>
          <el-radio-group v-model="sex" size="default" @change="load">
            <el-radio-button label="所有" value="所有" />
            <el-radio-button label="公" value="公" />
            <el-radio-button label="母" value="母" />
          </el-radio-group>
        </div>

        <div class="filter-group">
          <span class="filter-label">发布方</span>
          <el-radio-group v-model="category" size="default" @change="load">
            <el-radio-button label="所有" value="所有" />
            <el-radio-button label="官方" value="官方" />
            <el-radio-button label="个人" value="个人" />
          </el-radio-group>
        </div>

        <div class="filter-group grow">
          <span class="filter-label">地区</span>
          <el-input
            v-model="address"
            placeholder="输入地区筛选"
            class="filter-address"
            clearable
            @change="load"
            @clear="load"
          >
            <template #prefix><el-icon><Location /></el-icon></template>
          </el-input>
        </div>

        <div class="filter-actions">
          <el-input
            v-model="keyword"
            placeholder="搜索宠物名称…"
            class="filter-search"
            clearable
            @keyup.enter="load"
            @clear="load"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="load">搜索</el-button>
        </div>
      </div>

      <!-- 列表 -->
      <div v-loading="false" class="pet-list">
        <div v-if="pets.length === 0" class="empty-block">
          <div class="empty-icon"><Present /></div>
          <p>暂无符合条件的领养宠物，换个筛选条件试试吧</p>
          <el-button type="primary" round @click="router.push('/front/rescue')">发起救助</el-button>
        </div>

        <div v-else class="pets-grid">
          <div
            v-for="pet in pets"
            :key="pet.id"
            class="pet-card"
            @click="router.push('/front/petDetail?id=' + pet.id)"
          >
            <div class="pet-media">
              <img :src="pet.img" :alt="pet.name" class="pet-img" />
              <span class="pet-cat">{{ pet.category }}</span>
              <span class="pet-status" v-if="pet.status === '待领养'">待领养</span>
            </div>
            <div class="pet-body">
              <div class="pet-head">
                <h3 class="pet-name">{{ pet.name }}</h3>
                <span class="pet-sex" :class="pet.sex === '公' ? 'male' : 'female'">{{ pet.sex }}</span>
              </div>
              <div class="pet-tags">
                <span class="tag-item">{{ typeName(pet.typeId) || '未知品种' }}</span>
                <span class="tag-item">{{ pet.age }}</span>
              </div>
              <p class="pet-address">
                <el-icon><Location /></el-icon>
                {{ pet.address || '位置待确认' }}
              </p>
              <div class="pet-foot">
                <span class="pet-time">{{ pet.time }}</span>
                <span class="pet-cta">
                  了解详情 <el-icon><ArrowRight /></el-icon>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[9, 12, 30, 60]"
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
/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;

  &.grow {
    flex: 1;
  }

  .filter-label {
    font-size: 13px;
    color: var(--ink-500);
    flex-shrink: 0;
  }

  .filter-address {
    width: 180px;
  }
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 10px;

  .filter-search {
    width: 220px;
  }
}

.filter-bar :deep(.el-radio-button__inner) {
  border: none;
  border-radius: 8px !important;
  padding: 8px 16px;
  box-shadow: none;
  background: var(--bg-subtle);
  color: var(--ink-500);
}

.filter-bar :deep(.el-radio-button) {
  margin-right: 6px;
}

.filter-bar :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--brand-500);
  color: #fff;
}

/* 卡片网格 */
.pet-list {
  min-height: 320px;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
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
  height: 220px;
  overflow: hidden;
  background: var(--bg-subtle);

  .pet-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }

  .pet-cat,
  .pet-status {
    position: absolute;
    top: 12px;
    padding: 4px 12px;
    border-radius: var(--radius-pill);
    font-size: 12px;
    font-weight: 600;
    backdrop-filter: blur(4px);
  }

  .pet-cat {
    left: 12px;
    background: rgba(255, 255, 255, 0.92);
    color: var(--ink-700);
  }

  .pet-status {
    right: 12px;
    background: var(--brand-500);
    color: #fff;
  }
}

.pet-body {
  padding: 16px;
}

.pet-head {
  display: flex;
  align-items: center;
  gap: 8px;

  .pet-name {
    flex: 1;
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: var(--ink-900);
  }

  .pet-sex {
    font-size: 12px;
    font-weight: 600;
    padding: 2px 10px;
    border-radius: var(--radius-pill);

    &.male {
      background: var(--brand-50);
      color: var(--brand-600);
    }

    &.female {
      background: var(--danger-50);
      color: var(--danger-500);
    }
  }
}

.pet-tags {
  display: flex;
  gap: 8px;
  margin: 12px 0;

  .tag-item {
    padding: 3px 10px;
    border-radius: var(--radius-sm);
    background: var(--bg-subtle);
    color: var(--ink-500);
    font-size: 12.5px;
  }
}

.pet-address {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 0 0 14px;
  font-size: 13px;
  color: var(--ink-500);
}

.pet-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--line-light);

  .pet-time {
    font-size: 12.5px;
    color: var(--ink-400);
  }

  .pet-cta {
    display: inline-flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    font-weight: 500;
    color: var(--brand-600);
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0 8px;
}

@media (max-width: 1024px) {
  .pets-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .pets-grid {
    grid-template-columns: 1fr;
  }
}
</style>
