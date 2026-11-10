<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request.js'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from "element-plus"
import { ChatDotRound, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const trend = ref({})
const id = route.query.id
const load = () => {
  request.get("/trend/" + id).then(res => {
    trend.value = res.data
  })
}

const account = ref(localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {})
const commentItemId = route.query.id
const replyVisible = ref(false)
const comments = ref([])
const commentForm = ref({})

const loadComment = () => {
  request.get("/comment/tree/" + commentItemId).then(res => {
    comments.value = res.data
  })
}
loadComment()

const saveComment = () => {
  if (!account.value.id) {
    ElMessage.warning("请登录后操作")
    return
  }
  commentForm.value.itemId = commentItemId
  if (commentForm.value.contentReply) {
    commentForm.value.content = commentForm.value.contentReply
  }
  request.post("/comment", commentForm.value).then(res => {
    if (res.code === '200') {
      ElMessage.success('评论成功')
      commentForm.value = {}
      loadComment()
    } else {
      ElMessage.error('评论失败')
    }
  })
}

const delComment = (id) => {
  request.delete("/comment/" + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("删除成功")
      loadComment()
    } else {
      ElMessage.error("删除失败")
    }
  })
}

const handleReply = (pid) => {
  commentForm.value = { pid: pid }
  replyVisible.value = true
}

const cancelReply = () => {
  commentForm.value = { pid: '' }
  replyVisible.value = false
}

onMounted(() => {
  load()
  window.scrollTo(0, 0)
})
</script>

<template>
  <div class="page-shell">
    <div class="page-container narrow">
      <div class="page-header">
        <div class="breadcrumb">
          <a @click="router.back()"><el-icon class="back-icon"><ArrowLeft /></el-icon> 返回</a>
          <span class="sep">/</span>
          <router-link to="/front/home">首页</router-link>
          <span class="sep">/</span>
          <router-link to="/front/trend">团队动态</router-link>
        </div>
      </div>

      <article class="content-card article-main">
        <div class="article-cover" v-if="trend.img">
          <img :src="trend.img" :alt="trend.name" />
        </div>
        <div class="article-head">
          <span class="article-badge">团队动态</span>
          <h1 class="article-title">{{ trend.name }}</h1>
          <p class="article-meta">{{ trend.time }}</p>
        </div>
        <div v-rich-img class="rich-content" v-html="trend.content"></div>
      </article>

      <!-- 评论区 -->
      <div class="content-card comment-card">
        <div class="comment-head">
          <h2>
            <el-icon class="head-icon"><ChatDotRound /></el-icon>
            评论（{{ comments.length }}）
          </h2>
        </div>

        <div class="comment-editor">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="善语结善缘，恶语伤人心中…"
          />
          <div class="editor-actions">
            <el-button
              type="primary"
              :disabled="!commentForm.content"
              @click="saveComment"
            >
              发表评论
            </el-button>
          </div>
        </div>

        <div v-if="comments.length === 0" class="empty-block">
          <div class="empty-icon"><ChatDotRound /></div>
          <p>还没有评论，来说两句吧</p>
        </div>

        <div v-else class="comment-list">
          <div v-for="item in comments" :key="item.id" class="comment-item">
            <el-avatar :size="38" :src="item.avatarUrl" class="comment-avatar">
              {{ (item.nickname || 'U').slice(0, 1) }}
            </el-avatar>
            <div class="comment-body">
              <div class="comment-top">
                <span class="comment-name">{{ item.nickname }}</span>
                <span class="comment-time">{{ item.time }}</span>
              </div>
              <p class="comment-content">{{ item.content }}</p>
              <div class="comment-actions">
                <el-button link @click="handleReply(item.id)">回复</el-button>
                <el-button
                  link
                  type="danger"
                  @click="delComment(item.id)"
                  v-if="account.id === item.userId || account.role === 'ROLE_ADMIN'"
                >
                  删除
                </el-button>
              </div>

              <div class="reply-editor" v-if="commentForm.pid === item.id && replyVisible">
                <el-input v-model="commentForm.contentReply" placeholder="写下你的回复…" />
                <div class="reply-actions">
                  <el-button size="small" type="primary" @click="saveComment">确认</el-button>
                  <el-button size="small" @click="cancelReply">取消</el-button>
                </div>
              </div>

              <div v-if="item.children && item.children.length" class="reply-list">
                <div v-for="subItem in item.children" :key="subItem.id" class="reply-item">
                  <el-avatar :size="30" :src="subItem.avatarUrl" class="comment-avatar">
                    {{ (subItem.nickname || 'U').slice(0, 1) }}
                  </el-avatar>
                  <div class="comment-body">
                    <div class="comment-top">
                      <span class="comment-name">
                        {{ subItem.nickname }}
                        <span v-if="subItem.pid" class="reply-target">回复 @{{ subItem.pnickname }}</span>
                      </span>
                      <span class="comment-time">{{ subItem.time }}</span>
                    </div>
                    <p class="comment-content">{{ subItem.content }}</p>
                    <div class="comment-actions">
                      <el-button link @click="handleReply(subItem.id)">回复</el-button>
                      <el-button
                        link
                        type="danger"
                        @click="delComment(subItem.id)"
                        v-if="account.id === subItem.userId || account.role === 'ROLE_ADMIN'"
                      >
                        删除
                      </el-button>
                    </div>
                    <div class="reply-editor" v-if="commentForm.pid === subItem.id && replyVisible">
                      <el-input v-model="commentForm.contentReply" placeholder="写下你的回复…" />
                      <div class="reply-actions">
                        <el-button size="small" type="primary" @click="saveComment">确认</el-button>
                        <el-button size="small" @click="cancelReply">取消</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
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
  margin-bottom: 24px;
}

.article-cover {
  height: 320px;
  overflow: hidden;
  background: var(--bg-subtle);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.article-head {
  padding: 28px 36px 20px;
  border-bottom: 1px solid var(--line-light);
}

.article-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-pill);
  background: var(--brand-50);
  color: var(--brand-600);
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
  padding: 28px 36px 40px;
}

/* 评论区 */
.comment-card {
  padding: 24px;
}

.comment-head {
  h2 {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0 0 18px;
    font-size: 17px;
    font-weight: 600;
    color: var(--ink-900);
  }

  .head-icon {
    color: var(--brand-500);
  }
}

.comment-editor {
  background: var(--bg-subtle);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 24px;

  .editor-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--line-light);

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.comment-avatar {
  flex-shrink: 0;
  background: var(--brand-100);
  color: var(--brand-700);
  font-weight: 600;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-top {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 6px;

  .comment-name {
    font-size: 13.5px;
    font-weight: 600;
    color: var(--ink-700);
  }

  .reply-target {
    color: var(--brand-600);
    font-weight: 400;
    font-size: 12.5px;
  }

  .comment-time {
    font-size: 12px;
    color: var(--ink-400);
  }
}

.comment-content {
  margin: 0 0 8px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--ink-700);
}

.comment-actions {
  .el-button {
    font-size: 12.5px;
    color: var(--ink-400);
  }
}

.reply-editor {
  margin-top: 10px;
  padding: 10px;
  background: var(--bg-subtle);
  border-radius: var(--radius-sm);

  .reply-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
  }
}

.reply-list {
  margin-top: 14px;
  padding-left: 16px;
  border-left: 2px solid var(--line);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.reply-item {
  display: flex;
  gap: 10px;
}
</style>
