<template>
  <div class="welcome">
    <section class="hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="hero-badge">v2.0</div>
        <h1 class="hero-title">EnerPulse 能耗管理平台</h1>
        <p class="hero-desc">全场景能耗采集 · 实时监测 · 智能分析 · 精细化运营，助力企业构建绿色低碳的能源管理体系</p>
      </div>
    </section>

    <section class="section">
      <div class="section-header">
        <span class="section-bar"></span>
        <h2 class="section-title">快捷入口</h2>
      </div>
      <el-row :gutter="20">
        <el-col v-for="card in cards" :key="card.path" :xs="24" :sm="12" :md="8">
          <div class="quick-card" @click="goTo(card.path)">
            <div class="quick-icon-wrap" :style="{ background: card.bg }">
              <el-icon :size="26" color="#fff"><component :is="card.icon" /></el-icon>
            </div>
            <div class="quick-info">
              <div class="quick-title">{{ card.title }}</div>
              <div class="quick-desc">{{ card.desc }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="section">
      <div class="section-header">
        <span class="section-bar"></span>
        <h2 class="section-title">平台统计</h2>
      </div>
      <el-row :gutter="20">
        <el-col v-for="stat in stats" :key="stat.label" :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: stat.bg }">
              <el-icon :size="22" color="#fff"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-value" :style="{ color: stat.color }">
                {{ stat.value }}<span v-if="stat.unit" class="stat-unit">{{ stat.unit }}</span>
              </div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <footer class="footer">© 2024 EnerPulse 能耗管理平台 v2.0</footer>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Odometer, TrendCharts, Cpu, Warning, Setting, Monitor, Lightning, DataLine, Bell, Connection } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

interface QuickCard {
  title: string; desc: string; icon: any; bg: string; path: string
}
const cards: QuickCard[] = [
  { title: '数据看板', desc: '实时能耗数据可视化总览', icon: Odometer, bg: 'linear-gradient(135deg,#4F7CFF,#36D1DC)', path: '/dashboard' },
  { title: '能耗分析', desc: '多维度能耗趋势与占比分析', icon: TrendCharts, bg: 'linear-gradient(135deg,#2DCE89,#1FAE6F)', path: '/energy' },
  { title: '设备管理', desc: '设备台账与运行状态管理', icon: Cpu, bg: 'linear-gradient(135deg,#FFB236,#E69500)', path: '/devices' },
  { title: '报警记录', desc: '实时告警监控与历史记录', icon: Warning, bg: 'linear-gradient(135deg,#FF5757,#E03131)', path: '/alarms' },
  { title: '系统管理', desc: '用户、角色与系统配置管理', icon: Setting, bg: 'linear-gradient(135deg,#7C4FFF,#5B2DDB)', path: '/users' },
  { title: '云组态', desc: '云端组态画面与实时监控', icon: Monitor, bg: 'linear-gradient(135deg,#13C2C2,#0E96A0)', path: '/scada' }
]

const stats = reactive([
  { label: '今日能耗', value: '--', unit: 'kWh', color: '#4F7CFF', bg: 'linear-gradient(135deg,#4F7CFF,#36D1DC)', icon: DataLine },
  { label: '在线设备', value: '--', unit: '', color: '#2DCE89', bg: 'linear-gradient(135deg,#2DCE89,#1FAE6F)', icon: Connection },
  { label: '活跃告警', value: '--', unit: '', color: '#FF5757', bg: 'linear-gradient(135deg,#FF5757,#E03131)', icon: Bell },
  { label: '测点总数', value: '--', unit: '', color: '#FFB236', bg: 'linear-gradient(135deg,#FFB236,#E69500)', icon: Lightning }
])

function goTo(path: string) { router.push(path) }
function extractList(res: any): any[] {
  if (Array.isArray(res)) return res
  if (res?.items) return res.items
  if (res?.list) return res.list
  if (res?.records) return res.records
  return []
}
function extractTotal(res: any): number {
  if (typeof res === 'number') return res
  if (res?.total != null) return Number(res.total)
  if (res?.totalCount != null) return Number(res.totalCount)
  return extractList(res).length
}

async function loadStats() {
  request.get('/energy/consumption', { params: { period: 'DAY' } })
    .then((res: any) => {
      const val = typeof res === 'number' ? res : res?.totalConsumption ?? res?.consumption ?? res?.value ?? res?.total ?? 0
      stats[0].value = Number(val).toFixed(2)
    }).catch(() => {})
  request.get('/devices')
    .then((res: any) => {
      const list = extractList(res)
      stats[1].value = String(list.filter((d: any) => d.status === 'ONLINE').length)
    }).catch(() => {})
  request.get('/alarms', { params: { status: 'ACTIVE' } })
    .then((res: any) => { stats[2].value = String(extractTotal(res)) }).catch(() => {})
  request.get('/points')
    .then((res: any) => { stats[3].value = String(extractTotal(res)) }).catch(() => {})
}

onMounted(() => { loadStats() })
</script>

<style scoped>
.welcome { padding-bottom: 24px; }

.hero {
  position: relative;
  border-radius: 16px;
  padding: 56px 24px;
  text-align: center;
  color: #fff;
  margin-bottom: 28px;
  overflow: hidden;
  background: linear-gradient(135deg, #14172B 0%, #1E2240 40%, #2B3580 100%);
  box-shadow: 0 8px 32px rgba(20,23,43,.3);
}
.hero-overlay {
  position: absolute; inset: 0;
  background: radial-gradient(circle at 30% 50%, rgba(79,124,255,.3) 0%, transparent 60%),
              radial-gradient(circle at 70% 50%, rgba(54,209,220,.15) 0%, transparent 60%);
}
.hero-content { position: relative; z-index: 1; max-width: 760px; margin: 0 auto; }
.hero-badge {
  display: inline-block;
  margin-bottom: 16px;
  padding: 4px 16px;
  font-size: 12px;
  background: rgba(79,124,255,.2);
  border: 1px solid rgba(79,124,255,.3);
  border-radius: 999px;
  color: #8BB4FF;
  letter-spacing: .05em;
}
.hero-title { margin: 0; font-size: 34px; font-weight: 700; letter-spacing: .02em; }
.hero-desc { margin: 14px 0 0; font-size: 15px; opacity: .85; line-height: 1.7; }

.section { margin-bottom: 32px; }
.section-header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.section-bar { width: 4px; height: 20px; background: #4F7CFF; border-radius: 2px; }
.section-title { margin: 0; font-size: 18px; font-weight: 600; color: #1A1D29; }

.quick-card {
  background: #fff;
  border: 1px solid #E8ECF1;
  border-radius: 12px;
  padding: 24px 20px;
  margin-bottom: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  box-shadow: 0 1px 3px rgba(0,0,0,.04);
}
.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0,0,0,.1);
  border-color: transparent;
}
.quick-icon-wrap {
  width: 52px; height: 52px;
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,.1);
}
.quick-info { flex: 1; }
.quick-title { font-size: 16px; font-weight: 600; color: #1A1D29; margin-bottom: 4px; }
.quick-desc { font-size: 13px; color: #6B7280; line-height: 1.5; }

.stat-card {
  background: #fff;
  border: 1px solid #E8ECF1;
  border-radius: 12px;
  padding: 22px 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  box-shadow: 0 1px 3px rgba(0,0,0,.04);
}
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,.08); }
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,.08);
}
.stat-body { flex: 1; }
.stat-value { font-size: 26px; font-weight: 700; line-height: 1.2; }
.stat-unit { font-size: 13px; font-weight: 500; margin-left: 4px; opacity: .7; }
.stat-label { margin-top: 4px; font-size: 13px; color: #6B7280; }

.footer { text-align: center; color: #9CA3AF; font-size: 13px; padding: 28px 0 0; }
</style>