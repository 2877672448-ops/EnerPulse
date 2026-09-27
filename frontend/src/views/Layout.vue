<template>
  <el-container style="height: 100vh">
    <el-aside width="230px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon><Lightning /></el-icon>
        </div>
        <div class="logo-text">
          <span class="logo-name">EnerPulse</span>
          <span class="logo-sub">能耗管理平台</span>
        </div>
      </div>
      <el-scrollbar class="menu-scroll">
        <el-menu :default-active="$route.fullPath" :default-openeds="['/system','/devices','/energy','/scada','/wechat']" router background-color="transparent" text-color="#8B92A8" active-text-color="#fff">
          <el-menu-item index="/welcome"><el-icon><HomeFilled /></el-icon><span>欢迎页</span></el-menu-item>
          <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon><span>数据看板</span></el-menu-item>

          <el-sub-menu index="system">
            <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
            <el-menu-item index="/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
            <el-menu-item index="/areas"><el-icon><MapLocation /></el-icon><span>区域管理</span></el-menu-item>
            <el-menu-item index="/roles"><el-icon><UserFilled /></el-icon><span>角色管理</span></el-menu-item>
            <el-menu-item index="/dictionaries"><el-icon><Collection /></el-icon><span>字典管理</span></el-menu-item>
            <el-menu-item index="/peak-valley-flat"><el-icon><Timer /></el-icon><span>峰谷平设置</span></el-menu-item>
            <el-menu-item index="/tariffs"><el-icon><Money /></el-icon><span>费率管理</span></el-menu-item>
            <el-menu-item index="/operation-logs"><el-icon><Document /></el-icon><span>操作记录</span></el-menu-item>
            <el-menu-item index="/menus"><el-icon><Menu /></el-icon><span>菜单管理</span></el-menu-item>
            <el-menu-item index="/dashboard-config"><el-icon><Grid /></el-icon><span>看板管理</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="devices">
            <template #title><el-icon><Cpu /></el-icon><span>设备管理</span></template>
            <el-menu-item index="/devices?tab=gateway"><span>网关管理</span></el-menu-item>
            <el-menu-item index="/devices?tab=point"><span>点名管理</span></el-menu-item>
            <el-menu-item index="/devices?tab=device"><span>设备管理</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="energy">
            <template #title><el-icon><TrendCharts /></el-icon><span>能耗分析</span></template>
            <el-menu-item index="/energy?tab=history"><span>历史数据</span></el-menu-item>
            <el-menu-item index="/energy?tab=device"><span>设备数据</span></el-menu-item>
            <el-menu-item index="/energy?tab=consumption"><span>能源消耗</span></el-menu-item>
            <el-menu-item index="/energy?tab=analysis"><span>能耗分析</span></el-menu-item>
            <el-menu-item index="/energy?tab=ratio"><span>能耗占比</span></el-menu-item>
            <el-menu-item index="/energy?tab=yoy"><span>同比分析</span></el-menu-item>
            <el-menu-item index="/energy?tab=report"><span>报表下载</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="scada">
            <template #title><el-icon><Monitor /></el-icon><span>云组态</span></template>
            <el-menu-item index="/scada?tab=upload"><span>组态上传</span></el-menu-item>
            <el-menu-item index="/scada?tab=manage"><span>组态管理</span></el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/alarms"><el-icon><Warning /></el-icon><span>报警记录</span></el-menu-item>

          <el-sub-menu index="wechat">
            <template #title><el-icon><ChatDotRound /></el-icon><span>微信报警</span></template>
            <el-menu-item index="/wechat-alarm?tab=stats"><span>消息统计</span></el-menu-item>
            <el-menu-item index="/wechat-alarm?tab=users"><span>关注用户</span></el-menu-item>
            <el-menu-item index="/wechat-alarm?tab=history"><span>告警历史</span></el-menu-item>
            <el-menu-item index="/wechat-alarm?tab=device-bl"><span>设备黑名单</span></el-menu-item>
            <el-menu-item index="/wechat-alarm?tab=user-bl"><span>用户黑名单</span></el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="34" style="background:linear-gradient(135deg,#4F7CFF,#36D1DC);box-shadow:0 2px 8px rgba(79,124,255,.3)">{{ auth.user?.nickname?.[0] || 'A' }}</el-avatar>
            <span style="margin-left:10px;font-size:14px;font-weight:500">{{ auth.user?.nickname || '用户' }}</span>
            <el-icon style="margin-left:4px;color:#9CA3AF"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, Odometer, Cpu, TrendCharts, Warning, MapLocation, Setting, User, UserFilled, Collection, Money, Document, Timer, Menu, Grid, ArrowDown, SwitchButton, Monitor, ChatDotRound, Lightning } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const titleMap: Record<string, string> = {
  '/welcome': '欢迎页',
  '/dashboard': '数据看板',
  '/users': '用户管理',
  '/areas': '区域管理',
  '/roles': '角色管理',
  '/dictionaries': '字典管理',
  '/peak-valley-flat': '峰谷平设置',
  '/tariffs': '费率管理',
  '/operation-logs': '操作记录',
  '/menus': '菜单管理',
  '/dashboard-config': '看板管理',
  '/devices': '设备管理',
  '/energy': '能耗分析',
  '/scada': '云组态',
  '/alarms': '报警记录',
  '/wechat-alarm': '微信报警'
}
const currentTitle = computed(() => titleMap[route.path] || 'EnerPulse')

onMounted(() => {
  if (!auth.user) auth.fetchUser()
})

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    auth.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.sidebar {
  background: linear-gradient(180deg, #14172B 0%, #1A1E3A 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0,0,0,.15);
}
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.logo-icon {
  width: 38px; height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #4F7CFF 0%, #36D1DC 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(79,124,255,.4);
}
.logo-text { display: flex; flex-direction: column; }
.logo-name { color: #fff; font-size: 18px; font-weight: 700; letter-spacing: .02em; }
.logo-sub { font-size: 11px; color: #6B8FFF; margin-top: 1px; }

.menu-scroll { flex: 1; }

:deep(.el-menu) {
  border-right: none;
  padding: 8px 0;
}
:deep(.el-menu-item) {
  border-radius: 8px;
  margin: 2px 10px;
  height: 44px;
  line-height: 44px;
  transition: all 0.25s ease;
}
:deep(.el-menu-item:hover) {
  background: rgba(79,124,255,.1) !important;
  color: #fff !important;
}
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(79,124,255,.25) 0%, rgba(79,124,255,.1) 100%) !important;
  color: #fff !important;
  position: relative;
}
:deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #4F7CFF;
  border-radius: 0 3px 3px 0;
}
:deep(.el-sub-menu__title) {
  border-radius: 8px;
  margin: 2px 10px;
  height: 44px;
  line-height: 44px;
  transition: all 0.25s ease;
}
:deep(.el-sub-menu__title:hover) {
  background: rgba(79,124,255,.1) !important;
  color: #fff !important;
}
:deep(.el-sub-menu .el-menu-item) {
  padding-left: 48px !important;
}

.header {
  background: rgba(255,255,255,.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,.04);
  padding: 0 24px;
  border-bottom: 1px solid rgba(0,0,0,.04);
  z-index: 10;
}
.header-left { font-size: 15px; font-weight: 600; }
.user-info { display: flex; align-items: center; cursor: pointer; padding: 4px 8px; border-radius: 8px; transition: all 0.2s; }
.user-info:hover { background: rgba(0,0,0,.03); }

.main-content {
  background: #F5F7FA;
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease, transform 0.25s ease; }
.fade-enter-from { opacity: 0; transform: translateY(8px); }
.fade-leave-to { opacity: 0; transform: translateY(-8px); }
</style>