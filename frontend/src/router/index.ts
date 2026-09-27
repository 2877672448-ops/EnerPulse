import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/welcome',
    children: [
      { path: 'welcome', name: 'Welcome', component: () => import('@/views/Welcome.vue') },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'users', name: 'Users', component: () => import('@/views/UserManagement.vue') },
      { path: 'areas', name: 'Areas', component: () => import('@/views/AreaManagement.vue') },
      { path: 'roles', name: 'Roles', component: () => import('@/views/RoleManagement.vue') },
      { path: 'dictionaries', name: 'Dictionaries', component: () => import('@/views/DictionaryManagement.vue') },
      { path: 'peak-valley-flat', name: 'PeakValleyFlat', component: () => import('@/views/PeakValleyFlat.vue') },
      { path: 'tariffs', name: 'Tariffs', component: () => import('@/views/TariffManagement.vue') },
      { path: 'operation-logs', name: 'OperationLogs', component: () => import('@/views/OperationLog.vue') },
      { path: 'menus', name: 'Menus', component: () => import('@/views/MenuManagement.vue') },
      { path: 'dashboard-config', name: 'DashboardConfig', component: () => import('@/views/DashboardConfig.vue') },
      { path: 'devices', name: 'Devices', component: () => import('@/views/DeviceManagement.vue') },
      { path: 'energy', name: 'Energy', component: () => import('@/views/EnergyAnalysis.vue') },
      { path: 'scada', name: 'Scada', component: () => import('@/views/CloudScada.vue') },
      { path: 'alarms', name: 'Alarms', component: () => import('@/views/AlarmManagement.vue') },
      { path: 'wechat-alarm', name: 'WeChatAlarm', component: () => import('@/views/WeChatAlarm.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.path !== '/login' && !auth.token) {
    next('/login')
  } else if (to.path === '/login' && auth.token) {
    next('/')
  } else {
    next()
  }
})

export default router