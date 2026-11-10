import { createRouter, createWebHistory } from 'vue-router'
import { projectName } from '../../config/config.default'

const routes = [
  //通用路由
  {
    path: '/',
    name: '/',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue'),
    meta: {
      title: '404'
    }
  },
  //下面都是前台路由
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'),
    children: [
      // 前台子路由
      {
        path: 'home',
        name: 'FrontHome',
        component: () => import('../views/front/Home.vue'),
        meta: {
          title: '前台首页'
        }
      },
      {
        path: 'password',
        name: 'FrontPassword',
        component: () => import('../views/front/Password.vue'),
        meta: {
          title: '修改密码'
        }
      },
      {
        path: 'person',
        name: 'FrontPerson',
        component: () => import('../views/front/Person.vue'),
        meta: {
          title: '个人信息'
        }
      },
      {
        path: 'trendDetail',
        name: 'FrontTrendDetail',
        component: () => import('../views/front/TrendDetail.vue'),
        meta: {
          title: '团队动态详情'
        }
      },
      {
        path: 'rescue',
        name: 'FrontRescue',
        component: () => import('../views/front/Rescue.vue'),
        meta: {
          title: '救援求助'
        }
      },
      {
        path: 'article',
        name: 'FrontArticle',
        component: () => import('../views/front/Article.vue'),
        meta: {
          title: '百科知识'
        }
      },
      {
        path: 'articleDetail',
        name: 'FrontArticleDetail',
        component: () => import('../views/front/ArticleDetail.vue'),
        meta: {
          title: '百科详情'
        }
      },
      {
        path: 'trend',
        name: 'FrontTrend',
        component: () => import('../views/front/Trend.vue'),
        meta: {
          title: '团队动态'
        }
      },
      {
        path: 'myPet',
        name: 'FrontMyPet',
        component: () => import('../views/front/MyPet.vue'),
        meta: {
          title: '我的宠物'
        }
      },
      {
        path: 'pet',
        name: 'FrontPet',
        component: () => import('../views/front/Pet.vue'),
        meta: {
          title: '宠物领养'
        }
      },
      {
        path: 'petDetail',
        name: 'FrontPetDetail',
        component: () => import('../views/front/PetDetail.vue'),
        meta: {
          title: '宠物详情'
        }
      },
      {
        path: 'type',
        name: 'FrontType',
        component: () => import('../views/front/Type.vue'),
        meta: {
          title: '种类图鉴'
        }
      },
      {
        path: 'typeDetail',
        name: 'FrontTypeDetail',
        component: () => import('../views/front/TypeDetail.vue'),
        meta: {
          title: '图鉴详情'
        }
      },
      {
        path: 'application',
        name: 'FrontApplication',
        component: () => import('../views/front/Application.vue'),
        meta: {
          title: '领养申请'
        }
      },
      {
        path: 'myApplication',
        name: 'FrontMyApplication',
        component: () => import('../views/front/MyApplication.vue'),
        meta: {
          title: '我的领养申请'
        }
      },
      {
        path: 'myRescue',
        name: 'FrontMyRescue',
        component: () => import('../views/front/MyRescue.vue'),
        meta: {
          title: '我的救援求助'
        }
      },
      // 前台子路由
    ]
  },
  //下面都是后台路由
  {
    path: '/back',
    name: 'back',
    component: () => import('../views/Back.vue'),
    children: [
      // 后台子路由
      {
        path: 'home',
        name: 'BackHome',
        component: () => import('../views/back/Home.vue'),
        meta: {
          title: '后台首页'
        }
      },
      {
        path: 'password',
        name: 'BackPassword',
        component: () => import('../views/back/Password.vue'),
        meta: {
          title: '修改密码'
        }
      },
      {
        path: 'adminPerson',
        name: 'BackAdminPerson',
        component: () => import('../views/back/AdminPerson.vue'),
        meta: {
          title: '个人信息'
        }
      },
      {
        path: 'user',
        name: 'BackUser',
        component: () => import('../views/back/User.vue'),
        meta: {
          title: '用户管理'
        }
      },
      {
        path: 'admin',
        name: 'BackAdmin',
        component: () => import('../views/back/Admin.vue'),
        meta: {
          title: '管理员管理'
        }
      },
      {
        path: 'article',
        name: 'BackArticle',
        component: () => import('../views/back/Article.vue'),
        meta: {
          title: '文章管理'
        }
      },
      {
        path: 'comment',
        name: 'BackComment',
        component: () => import('../views/back/Comment.vue'),
        meta: {
          title: '评论管理'
        }
      },
      {
        path: 'pet',
        name: 'BackPet',
        component: () => import('../views/back/Pet.vue'),
        meta: {
          title: '宠物管理'
        }
      },
      {
        path: 'type',
        name: 'BackType',
        component: () => import('../views/back/Type.vue'),
        meta: {
          title: '宠物种类管理'
        }
      },
      {
        path: 'health',
        name: 'BackHealth',
        component: () => import('../views/back/Health.vue'),
        meta: {
          title: '健康状态管理'
        }
      },
      {
        path: 'application',
        name: 'BackApplication',
        component: () => import('../views/back/Application.vue'),
        meta: {
          title: '领养申请管理'
        }
      },
      {
        path: 'rescue',
        name: 'BackRescue',
        component: () => import('../views/back/Rescue.vue'),
        meta: {
          title: '宠物救援管理'
        }
      },
      {
        path: 'trend',
        name: 'BackTrend',
        component: () => import('../views/back/Trend.vue'),
        meta: {
          title: '团队动态管理'
        }
      },
      // 后台子路由
    ]
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const account = JSON.parse(localStorage.getItem("account") || '{}')
  if (to.matched.length === 0) {
    next('/404')
    return
  }
  if (to.path === '/') {
    if (account.role) {
      // 现在是只有角色为管理员才访问后台
      // 如果想设置其他角色登录后也默认访问后台，可以用下面的判断条件
      // account.role === 'ROLE_ADMIN' || account.role === 'ROLE_UNIT'
      if (account.role === 'ROLE_ADMIN') {
        next('/back/home')
      } else {
        next('/front/home')
      }
    } else {
      // 现在是只有登录以后才可以访问首页
      next('/login')
      // 如果想不登录就可以直接访问首页的话，直接用下面的跳转/front/home即可
      // next('/front/home')
    }
  } else {
    next()
  }
})

// 全局后置守卫
router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - ${projectName}` : projectName // 设置页面标题
})

export default router
