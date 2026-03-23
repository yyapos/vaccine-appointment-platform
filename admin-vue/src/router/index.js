import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/system/UserManagement.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'admins',
        name: 'Admins',
        component: () => import('@/views/system/AdminManagement.vue'),
        meta: { title: '管理员管理', icon: 'User' }
      },
      {
        path: 'children',
        name: 'Children',
        component: () => import('@/views/system/ChildManagement.vue'),
        meta: { title: '儿童管理', icon: 'Avatar' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/system/Settings.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      },
      {
        path: 'vaccines',
        name: 'Vaccines',
        component: () => import('@/views/vaccine/VaccineManagement.vue'),
        meta: { title: '疫苗管理', icon: 'Medicine' }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/appointment/AppointmentManagement.vue'),
        meta: { title: '预约管理', icon: 'Calendar' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/forum/ForumManagement.vue'),
        meta: { title: '论坛管理', icon: 'ChatDotRound' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/forum/AnnouncementManagement.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router