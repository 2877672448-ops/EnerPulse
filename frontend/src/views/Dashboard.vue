<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6"><el-card class="stat-card"><div class="stat-icon" style="background:#409EFF"><el-icon><Lightning /></el-icon></div><div class="stat-info"><div class="stat-label">今日总能耗</div><div class="stat-value">{{ stats.total }} kWh</div></div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-icon" style="background:#67C23A"><el-icon><Cpu /></el-icon></div><div class="stat-info"><div class="stat-label">在线设备</div><div class="stat-value">{{ stats.online }} / {{ stats.totalDevices }}</div></div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-icon" style="background:#E6A23C"><el-icon><Warning /></el-icon></div><div class="stat-info"><div class="stat-label">活跃告警</div><div class="stat-value">{{ stats.alarms }}</div></div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-icon" style="background:#F56C6C"><el-icon><TrendCharts /></el-icon></div><div class="stat-info"><div class="stat-label">峰值功率</div><div class="stat-value">{{ stats.peak }} kW</div></div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="16"><el-card><template #header>能耗趋势（近7天）</template><div ref="trendChart" style="height:320px"></div></el-card></el-col>
      <el-col :span="8"><el-card><template #header>能源占比</template><div ref="pieChart" style="height:320px"></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Lightning, Cpu, Warning, TrendCharts } from '@element-plus/icons-vue'
import { listDevices } from '@/api/device'
import { getAnalysis, getRatio } from '@/api/energy'
import { listAlarms } from '@/api/alarm'

const stats = reactive({ total: '0', online: 0, totalDevices: 0, alarms: 0, peak: 0 })
const trendChart = ref()
const pieChart = ref()

onMounted(async () => {
  const devices: any = await listDevices().catch(() => [])
  stats.totalDevices = devices.length
  stats.online = devices.filter((d: any) => d.status === 'ONLINE').length

  const alarms: any = await listAlarms({ status: 'ACTIVE', pageSize: 1 }).catch(() => ({ items: [], total: 0 }))
  stats.alarms = alarms?.total || 0

  const today = new Date()
  const startDate = new Date(today.getTime() - 6 * 86400000).toISOString().slice(0, 10)
  const endDate = today.toISOString().slice(0, 10)

  getAnalysis({ objectType: 'TENANT', objectId: 1, energyTypeId: 1, period: 'DAY', startDate, endDate }).then((res: any) => {
    stats.total = Number(res?.totalConsumption || 0).toFixed(2)
    const series = res?.series || []
    const peakVal = series.reduce((m: number, d: any) => Math.max(m, Number(d.consumption || 0)), 0)
    stats.peak = peakVal.toFixed(2)
    initTrendChart(series)
  }).catch(() => initTrendChart([]))

  getRatio({ energyTypeId: 1, startDate, endDate }).then((res: any) => {
    initPieChart(res?.items || [])
  }).catch(() => initPieChart([]))
})

function initTrendChart(series: any[]) {
  nextTick(() => {
    const dates = series.map((d: any) => d.date?.slice(5) || '')
    const values = series.map((d: any) => Number(d.consumption || 0))
    echarts.init(trendChart.value).setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value', name: 'kWh' },
      series: [{ type: 'line', smooth: true, areaStyle: {}, data: values, itemStyle: { color: '#409EFF' } }]
    })
  })
}

function initPieChart(items: any[]) {
  nextTick(() => {
    const nameMap: Record<number, string> = { 1: '电力', 2: '水', 3: '燃气', 4: '蒸汽' }
    const data = items.map((i: any) => ({
      value: Number(i.consumption || 0),
      name: nameMap[i.energyTypeId] || '类型' + i.energyTypeId
    }))
    echarts.init(pieChart.value).setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} kWh ({d}%)' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['40%', '70%'], data }]
    })
  })
}
</script>

<style scoped>
.stat-card { display: flex; align-items: center; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 28px; }
.stat-info { margin-left: 16px; }
.stat-label { color: #999; font-size: 13px; }
.stat-value { font-size: 24px; font-weight: 700; color: #303133; margin-top: 4px; }
</style>
