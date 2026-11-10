/**
 * v-rich-img 指令
 * 用于 v-html 渲染的富文本容器：
 * 1. 归一化后端返回的 127.0.0.1 图片前缀为当前 serverHost
 * 2. 图片加载失败时替换为默认宠物图（按顺序轮换，多处图片不同）
 */
import fallbackPet1 from '@/assets/宠物百科.png'
import fallbackPet2 from '@/assets/领养宠物.png'
import fallbackPet3 from '@/assets/宠物种类.webp'
import fallbackPet4 from '@/assets/团队动态.jpg'
import { serverHost } from '../../config/config.default'

const fallbackPool = [fallbackPet1, fallbackPet2, fallbackPet3, fallbackPet4]

function bind (el) {
  if (!el || !el.querySelectorAll) return
  el.querySelectorAll('img').forEach((img, index) => {
    // 归一化写死的 127.0.0.1 前缀，保证从当前服务地址访问
    if (img.src && img.src.indexOf('http://127.0.0.1:9090') === 0) {
      img.src = img.src.replace('http://127.0.0.1:9090', serverHost)
    }
    // 已绑定过则跳过（v-html 更新时会重复执行）
    if (img.getAttribute('data-rich-bound')) return
    img.setAttribute('data-rich-bound', '1')
    img.onerror = () => {
      img.src = fallbackPool[index % fallbackPool.length]
      img.onerror = null
    }
  })
}

export default {
  mounted (el) {
    bind(el)
  },
  updated (el) {
    bind(el)
  }
}
