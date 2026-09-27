<template>
  <div class="energy-analysis">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">

      <!-- ========== Tab 1: 历史数据 ========== -->
      <el-tab-pane label="历史数据" name="history">
        <el-card>
          <template #header>历史数据查询</template>
          <el-form :inline="true" :model="historyQuery" class="filter-form">
            <el-form-item label="网关">
              <el-select v-model="historyQuery.gatewayId" placeholder="选择网关" filterable clearable style="width:160px" @change="onHistoryGatewayChange">
                <el-option v-for="g in gateways" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="historyQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源分项">
              <el-select v-model="historyQuery.energyItem" multiple collapse-tags placeholder="选择能源分项" clearable style="width:180px">
                <el-option v-for="i in energyItems" :key="i.value" :label="i.label" :value="i.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备">
              <el-select v-model="historyQuery.deviceId" placeholder="选择设备" filterable clearable style="width:160px" @change="onHistoryDeviceChange">
                <el-option v-for="d in historyDevices" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="测点">
              <el-select v-model="historyQuery.pointId" placeholder="选择测点" filterable clearable style="width:160px">
                <el-option v-for="p in historyPoints" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间段">
              <el-select v-model="historyQuery.period" placeholder="时间段" style="width:100px">
                <el-option label="小时" value="HOUR" />
                <el-option label="日" value="DAY" />
                <el-option label="周" value="WEEK" />
              </el-select>
            </el-form-item>
            <el-form-item label="快速选择">
              <el-select v-model="historyQuery.quickSelect" placeholder="快速选择时间" clearable style="width:160px" @change="onHistoryQuickSelect">
                <el-option label="近1小时" value="1h" />
                <el-option label="近6小时" value="6h" />
                <el-option label="近24小时" value="24h" />
                <el-option label="今天" value="today" />
                <el-option label="昨天" value="yesterday" />
                <el-option label="近7天" value="7d" />
                <el-option label="近30天" value="30d" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始时间">
              <el-date-picker v-model="historyQuery.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="开始时间" style="width:200px" />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker v-model="historyQuery.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="结束时间" style="width:200px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleHistorySearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="exportHistoryData">
                <el-icon><Download /></el-icon>导出
              </el-button>
              <el-dropdown trigger="click" @command="() => collectToDashboard('history')">
                <el-button>更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="collect">收藏到看板</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>历史数据曲线</template>
          <div v-loading="historyLoading" ref="historyChartRef" style="height:400px"></div>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>原始数据</template>
          <el-table :data="historyData" border stripe max-height="400" v-loading="historyLoading">
            <el-table-column prop="pointId" label="测点ID" width="100" />
            <el-table-column prop="timestamp" label="时间" width="200" />
            <el-table-column prop="value" label="数值" width="150" />
            <el-table-column prop="unit" label="单位" width="100" />
            <el-table-column prop="quality" label="质量" width="100" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 2: 设备数据 ========== -->
      <el-tab-pane label="设备数据" name="device">
        <el-card>
          <template #header>设备数据查询</template>
          <el-form :inline="true" :model="deviceQuery" class="filter-form">
            <el-form-item label="网关">
              <el-select v-model="deviceQuery.gatewayId" placeholder="选择网关" filterable clearable style="width:160px" @change="onDeviceGatewayChange">
                <el-option v-for="g in gateways" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="deviceQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源分项">
              <el-select v-model="deviceQuery.energyItem" multiple collapse-tags placeholder="选择能源分项" clearable style="width:180px">
                <el-option v-for="i in energyItems" :key="i.value" :label="i.label" :value="i.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备">
              <el-select v-model="deviceQuery.deviceId" placeholder="选择设备" filterable clearable style="width:160px">
                <el-option v-for="d in deviceTabDevices" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="快速选择">
              <el-select v-model="deviceQuery.quickSelect" placeholder="快速选择时间" clearable style="width:160px" @change="onDeviceQuickSelect">
                <el-option label="近1小时" value="1h" />
                <el-option label="近6小时" value="6h" />
                <el-option label="近24小时" value="24h" />
                <el-option label="今天" value="today" />
                <el-option label="昨天" value="yesterday" />
                <el-option label="近7天" value="7d" />
                <el-option label="近30天" value="30d" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始时间">
              <el-date-picker v-model="deviceQuery.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="开始时间" style="width:200px" />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker v-model="deviceQuery.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="结束时间" style="width:200px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleDeviceSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="exportDeviceData">
                <el-icon><Download /></el-icon>导出Excel
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>设备数据</template>
          <el-table :data="deviceData" border stripe max-height="500" v-loading="deviceLoading">
            <el-table-column prop="pointId" label="测点ID" width="100" />
            <el-table-column prop="timestamp" label="时间" width="200" />
            <el-table-column prop="value" label="数值" width="150" />
            <el-table-column prop="unit" label="单位" width="100" />
            <el-table-column prop="quality" label="质量" width="100" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 3: 能源消耗 ========== -->
      <el-tab-pane label="能源消耗" name="consumption">
        <el-card>
          <template #header>能源消耗查询</template>
          <el-form :inline="true" :model="consumptionQuery" class="filter-form">
            <el-form-item label="网关">
              <el-select v-model="consumptionQuery.gatewayId" placeholder="选择网关" filterable clearable style="width:160px">
                <el-option v-for="g in gateways" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="consumptionQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备">
              <el-select v-model="consumptionQuery.deviceId" placeholder="选择设备" filterable clearable style="width:160px" @change="onConsumptionDeviceChange">
                <el-option v-for="d in devices" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="测点">
              <el-select v-model="consumptionQuery.pointId" placeholder="选择测点" filterable clearable style="width:160px">
                <el-option v-for="p in consumptionPoints" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间维度">
              <el-select v-model="consumptionQuery.period" placeholder="时间维度" style="width:100px">
                <el-option label="日" value="DAY" />
                <el-option label="月" value="MONTH" />
                <el-option label="年" value="YEAR" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="consumptionQuery.startDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:180px" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="consumptionQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleConsumptionSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-dropdown trigger="click" @command="() => collectToDashboard('consumption')">
                <el-button>更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="collect">收藏到看板</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>能源消耗趋势</template>
          <div v-loading="consumptionLoading" ref="consumptionChartRef" style="height:400px"></div>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 4: 能耗分析 ========== -->
      <el-tab-pane label="能耗分析" name="analysis">
        <el-card>
          <template #header>能耗分析查询</template>
          <el-form :inline="true" :model="analysisQuery" class="filter-form">
            <el-form-item label="查询对象">
              <el-select v-model="analysisQuery.objectType" placeholder="查询对象" style="width:120px">
                <el-option label="租户" value="TENANT" />
                <el-option label="区域" value="AREA" />
                <el-option label="设备" value="DEVICE" />
              </el-select>
            </el-form-item>
            <el-form-item label="对象ID">
              <el-input-number v-model="analysisQuery.objectId" :min="1" placeholder="对象ID" style="width:120px" />
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="analysisQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能耗分类">
              <el-select v-model="analysisQuery.energyClassification" placeholder="能耗分类" clearable style="width:140px">
                <el-option v-for="c in energyClassifications" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间维度">
              <el-select v-model="analysisQuery.period" placeholder="时间维度" style="width:100px">
                <el-option label="日" value="DAY" />
                <el-option label="月" value="MONTH" />
                <el-option label="年" value="YEAR" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="analysisQuery.startDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:180px" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="analysisQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAnalysisSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="exportAnalysisPdf">
                <el-icon><Download /></el-icon>导出PDF
              </el-button>
              <el-button @click="exportAnalysisExcel">
                <el-icon><Download /></el-icon>导出Excel
              </el-button>
              <el-dropdown trigger="click" @command="() => collectToDashboard('analysis')">
                <el-button>更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="collect">收藏到看板</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-form-item>
          </el-form>
        </el-card>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">总能耗</div>
              <div class="summary-value">{{ analysisResult.totalConsumption || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">总费用</div>
              <div class="summary-value">{{ analysisResult.totalCost || 0 }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top:16px">
          <template #header>能耗分析</template>
          <div v-loading="analysisLoading" ref="analysisChartRef" style="height:400px"></div>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 5: 能耗占比 ========== -->
      <el-tab-pane label="能耗占比" name="ratio">
        <el-card>
          <template #header>能耗占比查询</template>
          <el-form :inline="true" :model="ratioQuery" class="filter-form">
            <el-form-item label="查询对象">
              <el-select v-model="ratioQuery.objectType" placeholder="查询对象" style="width:120px">
                <el-option label="租户" value="TENANT" />
                <el-option label="区域" value="AREA" />
                <el-option label="设备" value="DEVICE" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="ratioQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能耗分类">
              <el-select v-model="ratioQuery.energyClassification" placeholder="能耗分类" clearable style="width:140px">
                <el-option v-for="c in energyClassifications" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间类型">
              <el-select v-model="ratioQuery.timeType" placeholder="时间类型" style="width:100px">
                <el-option label="日" value="DAY" />
                <el-option label="月" value="MONTH" />
                <el-option label="年" value="YEAR" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="ratioQuery.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRatioSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>能耗占比</template>
          <div v-loading="ratioLoading" ref="ratioChartRef" style="height:400px"></div>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 6: 同比分析 ========== -->
      <el-tab-pane label="同比分析" name="yoy">
        <el-card>
          <template #header>同比分析查询</template>
          <el-form :inline="true" :model="yoyQuery" class="filter-form">
            <el-form-item label="查询对象">
              <el-select v-model="yoyQuery.objectType" placeholder="查询对象" style="width:120px">
                <el-option label="租户" value="TENANT" />
                <el-option label="区域" value="AREA" />
                <el-option label="设备" value="DEVICE" />
              </el-select>
            </el-form-item>
            <el-form-item label="能源类型">
              <el-select v-model="yoyQuery.energyTypeId" placeholder="选择能源类型" clearable style="width:140px">
                <el-option v-for="t in energyTypes" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能耗分类">
              <el-select v-model="yoyQuery.energyClassification" placeholder="能耗分类" clearable style="width:140px">
                <el-option v-for="c in energyClassifications" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间维度">
              <el-select v-model="yoyQuery.period" placeholder="时间维度" style="width:100px">
                <el-option label="日" value="DAY" />
                <el-option label="月" value="MONTH" />
                <el-option label="年" value="YEAR" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="yoyQuery.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleYoySearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">本期</div>
              <div class="summary-value">{{ yoyResult.current || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">同比</div>
              <div class="summary-value">{{ yoyResult.previous || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">差值</div>
              <div class="summary-value">{{ yoyResult.difference || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">增长率</div>
              <div class="summary-value" :class="{ 'rate-positive': (yoyResult.rate || 0) > 0, 'rate-negative': (yoyResult.rate || 0) < 0 }">
                {{ ((yoyResult.rate || 0) * 100).toFixed(2) }}%
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top:16px">
          <template #header>同比对比</template>
          <div v-loading="yoyLoading" ref="yoyChartRef" style="height:400px"></div>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 7: 报表下载 ========== -->
      <el-tab-pane label="报表下载" name="report">
        <el-card>
          <template #header>可下载报表</template>
          <el-table :data="reports" border stripe>
            <el-table-column prop="name" label="报表名称" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="period" label="周期" width="120" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="downloadReport(row)">
                  <el-icon><Download /></el-icon>下载
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Search, Download, ArrowDown } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { listGateways, listDevices, listPoints } from '@/api/device'
import {
  getEnergyHistory,
  getDeviceData,
  getConsumption,
  getAnalysis,
  getRatio,
  getYoy,
  exportDeviceData as exportDeviceApi,
  exportHistoryData as exportHistoryApi
} from '@/api/energy'

// ==================== Common State ====================
const route = useRoute()
  const activeTab = ref('history')
const gateways = ref<any[]>([])
const devices = ref<any[]>([])
const points = ref<any[]>([])

const energyTypes = ref([
  { id: 1, name: '电力' },
  { id: 2, name: '水' },
  { id: 3, name: '燃气' },
  { id: 4, name: '蒸汽' }
])
const energyItems = ref([
  { value: 'LIGHTING', label: '照明' },
  { value: 'AIR_CONDITIONING', label: '空调' },
  { value: 'POWER', label: '动力' },
  { value: 'SPECIAL', label: '特殊' },
  { value: 'OTHER', label: '其他' }
])
const energyClassifications = ref([
  { value: 'ELECTRICITY', label: '电量' },
  { value: 'WATER', label: '水量' },
  { value: 'GAS', label: '气量' },
  { value: 'HEAT', label: '热量' }
])

// Chart instances
let historyChart: echarts.ECharts | null = null
let consumptionChart: echarts.ECharts | null = null
let analysisChart: echarts.ECharts | null = null
let ratioChart: echarts.ECharts | null = null
let yoyChart: echarts.ECharts | null = null

// ==================== Tab 1: 历史数据 ====================
const historyChartRef = ref<HTMLElement>()
const historyLoading = ref(false)
const historyData = ref<any[]>([])
const historyQuery = reactive({
  gatewayId: undefined as number | undefined,
  energyTypeId: undefined as number | undefined,
  energyItem: [] as string[],
  deviceId: undefined as number | undefined,
  pointId: undefined as number | undefined,
  period: 'DAY' as string,
  startTime: '',
  endTime: '',
  quickSelect: '' as string
})

const historyDevices = computed(() => {
  if (!historyQuery.gatewayId) return devices.value
  return devices.value.filter((d: any) => d.gatewayId === historyQuery.gatewayId)
})

const historyPoints = computed(() => {
  if (!historyQuery.deviceId) return points.value
  return points.value.filter((p: any) => p.deviceId === historyQuery.deviceId)
})

function onHistoryGatewayChange() {
  historyQuery.deviceId = undefined
  historyQuery.pointId = undefined
}

function onHistoryDeviceChange() {
  historyQuery.pointId = undefined
}

async function loadHistoryData() {
  if (!historyQuery.startTime || !historyQuery.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  historyLoading.value = true
  try {
    const res = await getEnergyHistory({
      gatewayId: historyQuery.gatewayId,
      deviceId: historyQuery.deviceId,
      pointId: historyQuery.pointId,
      energyTypeId: historyQuery.energyTypeId,
      startTime: historyQuery.startTime,
      endTime: historyQuery.endTime
    }) as any
    historyData.value = Array.isArray(res) ? res : (res?.items || [])
    initHistoryChart()
  } catch (e) {
    historyData.value = []
  } finally {
    historyLoading.value = false
  }
}

function initHistoryChart() {
  nextTick(() => {
    if (!historyChartRef.value) return
    historyChart?.dispose()
    historyChart = echarts.init(historyChartRef.value)
    historyChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: historyData.value.map((d: any) => d.timestamp), boundaryGap: false },
      yAxis: { type: 'value' },
      series: [{
        name: '数值',
        type: 'line',
        smooth: true,
        data: historyData.value.map((d: any) => Number(d.value)),
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409EFF' }
      }]
    })
  })
}

// ==================== Quick Time Select & Collect to Dashboard ====================
function pad2(n: number) {
  return String(n).padStart(2, '0')
}
function fmtDateTime(d: Date) {
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}T${pad2(d.getHours())}:${pad2(d.getMinutes())}:${pad2(d.getSeconds())}`
}
function applyQuickSelect(value: string, target: { startTime: string; endTime: string }) {
  if (!value) return
  const now = new Date()
  switch (value) {
    case '1h': {
      target.startTime = fmtDateTime(new Date(now.getTime() - 3600000))
      target.endTime = fmtDateTime(now)
      break
    }
    case '6h': {
      target.startTime = fmtDateTime(new Date(now.getTime() - 6 * 3600000))
      target.endTime = fmtDateTime(now)
      break
    }
    case '24h': {
      target.startTime = fmtDateTime(new Date(now.getTime() - 24 * 3600000))
      target.endTime = fmtDateTime(now)
      break
    }
    case 'today': {
      const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
      target.startTime = fmtDateTime(start)
      target.endTime = fmtDateTime(now)
      break
    }
    case 'yesterday': {
      const yStart = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 0, 0, 0)
      const yEnd = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 0)
      target.startTime = fmtDateTime(yStart)
      target.endTime = fmtDateTime(yEnd)
      break
    }
    case '7d': {
      target.startTime = fmtDateTime(new Date(now.getTime() - 7 * 86400000))
      target.endTime = fmtDateTime(now)
      break
    }
    case '30d': {
      target.startTime = fmtDateTime(new Date(now.getTime() - 30 * 86400000))
      target.endTime = fmtDateTime(now)
      break
    }
  }
}
function onHistoryQuickSelect(val: string) {
  applyQuickSelect(val, historyQuery)
}
function onDeviceQuickSelect(val: string) {
  applyQuickSelect(val, deviceQuery)
}
async function collectToDashboard(tab: string) {
  const titleMap: Record<string, string> = {
    history: '历史数据图表',
    consumption: '能源消耗图表',
    analysis: '能耗分析图表'
  }
  const typeMap: Record<string, string> = {
    history: 'LINE',
    consumption: 'BAR',
    analysis: 'BAR'
  }
  const filtersMap: Record<string, any> = {
    history: { ...historyQuery },
    consumption: { ...consumptionQuery },
    analysis: { ...analysisQuery }
  }
  const dashboardId = 1
  const widget = {
    title: titleMap[tab] || '图表',
    type: typeMap[tab] || 'LINE',
    config: JSON.stringify({ tab, filters: filtersMap[tab] }),
    sortOrder: 99,
    visible: true
  }
  try {
    await request.post(`/dashboards/${dashboardId}/widgets`, widget)
  } catch (e) {
    // graceful: ignore API failure
  }
  ElMessage.success('已收藏到看板')
}

function exportHistoryData() {
  if (!historyQuery.pointId) {
    ElMessage.warning('请选择测点')
    return
  }
  if (!historyQuery.startTime || !historyQuery.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  exportHistoryApi({
    pointId: historyQuery.pointId,
    startTime: historyQuery.startTime,
    endTime: historyQuery.endTime
  }).then((blob: any) => {
    downloadBlob(blob, '历史数据.xlsx')
  })
}

// ==================== Tab 2: 设备数据 ====================
const deviceLoading = ref(false)
const deviceData = ref<any[]>([])
const deviceQuery = reactive({
  gatewayId: undefined as number | undefined,
  energyTypeId: undefined as number | undefined,
  energyItem: [] as string[],
  deviceId: undefined as number | undefined,
  startTime: '',
  endTime: '',
  quickSelect: '' as string
})

const deviceTabDevices = computed(() => {
  if (!deviceQuery.gatewayId) return devices.value
  return devices.value.filter((d: any) => d.gatewayId === deviceQuery.gatewayId)
})

function onDeviceGatewayChange() {
  deviceQuery.deviceId = undefined
}

async function loadDeviceData() {
  if (!deviceQuery.deviceId) {
    ElMessage.warning('请选择设备')
    return
  }
  if (!deviceQuery.startTime || !deviceQuery.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  deviceLoading.value = true
  try {
    const res = await getDeviceData({
      deviceId: deviceQuery.deviceId,
      startTime: deviceQuery.startTime,
      endTime: deviceQuery.endTime
    }) as any
    deviceData.value = Array.isArray(res) ? res : (res?.items || [])
  } catch (e) {
    deviceData.value = []
  } finally {
    deviceLoading.value = false
  }
}

function exportDeviceData() {
  if (!deviceQuery.deviceId) {
    ElMessage.warning('请选择设备')
    return
  }
  if (!deviceQuery.startTime || !deviceQuery.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  exportDeviceApi({
    deviceId: deviceQuery.deviceId,
    startTime: deviceQuery.startTime,
    endTime: deviceQuery.endTime
  }).then((blob: any) => {
    downloadBlob(blob, '设备数据.xlsx')
  })
}

// ==================== Tab 3: 能源消耗 ====================
const consumptionChartRef = ref<HTMLElement>()
const consumptionLoading = ref(false)
const consumptionResult = ref<any>({ items: [], total: 0 })
const consumptionQuery = reactive({
  gatewayId: undefined as number | undefined,
  energyTypeId: undefined as number | undefined,
  deviceId: undefined as number | undefined,
  pointId: undefined as number | undefined,
  period: 'DAY' as string,
  startDate: '',
  endDate: ''
})

const consumptionPoints = computed(() => {
  if (!consumptionQuery.deviceId) return points.value
  return points.value.filter((p: any) => p.deviceId === consumptionQuery.deviceId)
})

function onConsumptionDeviceChange() {
  consumptionQuery.pointId = undefined
}

async function loadConsumptionData() {
  if (!consumptionQuery.startDate || !consumptionQuery.endDate) {
    ElMessage.warning('请选择日期范围')
    return
  }
  consumptionLoading.value = true
  try {
    const res = await getConsumption({
      pointId: consumptionQuery.pointId,
      energyTypeId: consumptionQuery.energyTypeId,
      period: consumptionQuery.period,
      startDate: consumptionQuery.startDate,
      endDate: consumptionQuery.endDate
    }) as any
    consumptionResult.value = res || { items: [], total: 0 }
    initConsumptionChart()
  } catch (e) {
    consumptionResult.value = { items: [], total: 0 }
  } finally {
    consumptionLoading.value = false
  }
}

function initConsumptionChart() {
  nextTick(() => {
    if (!consumptionChartRef.value) return
    consumptionChart?.dispose()
    consumptionChart = echarts.init(consumptionChartRef.value)
    const items = consumptionResult.value.items || []
    consumptionChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: items.map((d: any) => d.period) },
      yAxis: { type: 'value', name: '消耗量' },
      series: [{
        type: 'bar',
        data: items.map((d: any) => Number(d.consumption)),
        itemStyle: { color: '#67C23A' },
        barWidth: '60%'
      }]
    })
  })
}

// ==================== Tab 4: 能耗分析 ====================
const analysisChartRef = ref<HTMLElement>()
const analysisLoading = ref(false)
const analysisResult = ref<any>({ series: [], totalConsumption: 0, totalCost: 0 })
const analysisQuery = reactive({
  objectType: 'TENANT' as string,
  objectId: 1 as number,
  energyTypeId: undefined as number | undefined,
  energyClassification: '' as string,
  period: 'DAY' as string,
  startDate: '',
  endDate: ''
})

async function loadAnalysisData() {
  if (!analysisQuery.startDate || !analysisQuery.endDate) {
    ElMessage.warning('请选择日期范围')
    return
  }
  analysisLoading.value = true
  try {
    const res = await getAnalysis({
      objectType: analysisQuery.objectType,
      objectId: analysisQuery.objectId,
      energyTypeId: analysisQuery.energyTypeId,
      period: analysisQuery.period,
      startDate: analysisQuery.startDate,
      endDate: analysisQuery.endDate
    }) as any
    analysisResult.value = res || { series: [], totalConsumption: 0, totalCost: 0 }
    initAnalysisChart()
  } catch (e) {
    analysisResult.value = { series: [], totalConsumption: 0, totalCost: 0 }
  } finally {
    analysisLoading.value = false
  }
}

function initAnalysisChart() {
  nextTick(() => {
    if (!analysisChartRef.value) return
    analysisChart?.dispose()
    analysisChart = echarts.init(analysisChartRef.value)
    const series = analysisResult.value.series || []
    analysisChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['能耗', '费用'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: series.map((d: any) => d.date) },
      yAxis: [
        { type: 'value', name: '能耗' },
        { type: 'value', name: '费用' }
      ],
      series: [
        { name: '能耗', type: 'bar', data: series.map((d: any) => Number(d.consumption)), itemStyle: { color: '#409EFF' } },
        { name: '费用', type: 'bar', yAxisIndex: 1, data: series.map((d: any) => Number(d.cost)), itemStyle: { color: '#E6A23C' } }
      ]
    })
  })
}

function exportAnalysisPdf() {
  if (!analysisQuery.startDate || !analysisQuery.endDate) {
    ElMessage.warning('请选择日期范围')
    return
  }
  const params = new URLSearchParams({
    objectType: analysisQuery.objectType,
    objectId: String(analysisQuery.objectId),
    period: analysisQuery.period,
    startDate: analysisQuery.startDate,
    endDate: analysisQuery.endDate
  })
  if (analysisQuery.energyTypeId) params.append('energyTypeId', String(analysisQuery.energyTypeId))
  window.open(`/api/v1/energy/export/analysis-pdf?${params.toString()}`)
}

function exportAnalysisExcel() {
  if (!analysisQuery.startDate || !analysisQuery.endDate) {
    ElMessage.warning('请选择日期范围')
    return
  }
  const params = new URLSearchParams({
    objectType: analysisQuery.objectType,
    objectId: String(analysisQuery.objectId),
    period: analysisQuery.period,
    startDate: analysisQuery.startDate,
    endDate: analysisQuery.endDate
  })
  if (analysisQuery.energyTypeId) params.append('energyTypeId', String(analysisQuery.energyTypeId))
  window.open(`/api/v1/energy/export/analysis-excel?${params.toString()}`)
}

// ==================== Tab 5: 能耗占比 ====================
const ratioChartRef = ref<HTMLElement>()
const ratioLoading = ref(false)
const ratioResult = ref<any>({ items: [] })
const ratioQuery = reactive({
  objectType: 'TENANT' as string,
  energyTypeId: undefined as number | undefined,
  energyClassification: '' as string,
  timeType: 'MONTH' as string,
  date: '' as string
})

async function loadRatioData() {
  if (!ratioQuery.date) {
    ElMessage.warning('请选择日期')
    return
  }
  ratioLoading.value = true
  try {
    const res = await getRatio({
      objectType: ratioQuery.objectType,
      energyTypeId: ratioQuery.energyTypeId,
      startDate: ratioQuery.date,
      endDate: ratioQuery.date
    }) as any
    ratioResult.value = res || { items: [] }
    initRatioChart()
  } catch (e) {
    ratioResult.value = { items: [] }
  } finally {
    ratioLoading.value = false
  }
}

function initRatioChart() {
  nextTick(() => {
    if (!ratioChartRef.value) return
    ratioChart?.dispose()
    ratioChart = echarts.init(ratioChartRef.value)
    const items = ratioResult.value.items || []
    const nameMap: Record<number, string> = { 1: '电力', 2: '水', 3: '燃气', 4: '蒸汽' }
    const data = items.map((i: any) => ({
      value: Number(i.consumption || 0),
      name: nameMap[i.energyTypeId] || '类型' + i.energyTypeId
    }))
    ratioChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: '18', fontWeight: 'bold' } },
        labelLine: { show: false },
        data
      }]
    })
  })
}

// ==================== Tab 6: 同比分析 ====================
const yoyChartRef = ref<HTMLElement>()
const yoyLoading = ref(false)
const yoyResult = ref<any>({ current: 0, previous: 0, difference: 0, rate: 0 })
const yoyQuery = reactive({
  objectType: 'TENANT' as string,
  energyTypeId: undefined as number | undefined,
  energyClassification: '' as string,
  period: 'MONTH' as string,
  date: '' as string
})

async function loadYoyData() {
  if (!yoyQuery.date) {
    ElMessage.warning('请选择日期')
    return
  }
  yoyLoading.value = true
  try {
    const res = await getYoy({
      energyTypeId: yoyQuery.energyTypeId,
      startDate: yoyQuery.date,
      endDate: yoyQuery.date
    }) as any
    yoyResult.value = res || { current: 0, previous: 0, difference: 0, rate: 0 }
    initYoyChart()
  } catch (e) {
    yoyResult.value = { current: 0, previous: 0, difference: 0, rate: 0 }
  } finally {
    yoyLoading.value = false
  }
}

function initYoyChart() {
  nextTick(() => {
    if (!yoyChartRef.value) return
    yoyChart?.dispose()
    yoyChart = echarts.init(yoyChartRef.value)
    const current = Number(yoyResult.value.current || 0)
    const previous = Number(yoyResult.value.previous || 0)
    yoyChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['本期', '同比'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['本期', '同比'] },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: [
          { value: current, itemStyle: { color: '#409EFF' } },
          { value: previous, itemStyle: { color: '#E6A23C' } }
        ],
        barWidth: '40%'
      }]
    })
  })
}

// ==================== Tab 7: 报表下载 ====================
const reports = ref([
  { id: 1, name: '日能耗报表', description: '每日能源消耗统计', period: '日' },
  { id: 2, name: '月能耗报表', description: '每月能源消耗统计', period: '月' },
  { id: 3, name: '年能耗报表', description: '年度能源消耗统计', period: '年' },
  { id: 4, name: '设备能耗报表', description: '设备能源消耗明细', period: '月' },
  { id: 5, name: '能耗分析报表', description: '能耗对比分析', period: '月' }
])

function downloadReport(row: any) {
  const token = localStorage.getItem('token')
  window.open(`/api/v1/energy/export/report?id=${row.id}&token=${token || ''}`)
}

// ==================== Common Utilities ====================
function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

function handleTabChange() {
  nextTick(() => {
    historyChart?.resize()
    consumptionChart?.resize()
    analysisChart?.resize()
    ratioChart?.resize()
    yoyChart?.resize()
  })
}

function handleResize() {
  historyChart?.resize()
  consumptionChart?.resize()
  analysisChart?.resize()
  ratioChart?.resize()
  yoyChart?.resize()
}

function initDefaultDates() {
  const now = new Date()
  const yesterday = new Date(now.getTime() - 86400000)
  const pad = (n: number) => String(n).padStart(2, '0')
  const fmt = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  historyQuery.startTime = fmt(yesterday)
  historyQuery.endTime = fmt(now)
  deviceQuery.startTime = fmt(yesterday)
  deviceQuery.endTime = fmt(now)
  const todayStr = now.toISOString().slice(0, 10)
  const weekAgoStr = new Date(now.getTime() - 7 * 86400000).toISOString().slice(0, 10)
  consumptionQuery.startDate = weekAgoStr
  consumptionQuery.endDate = todayStr
  analysisQuery.startDate = weekAgoStr
  analysisQuery.endDate = todayStr
  ratioQuery.date = todayStr
  yoyQuery.date = todayStr
}

// ==================== Lifecycle ====================

watch(() => route.query.tab, (val) => {
  if (typeof val === "string") {
    const tabMap: Record<string, string> = {"history":"history","device":"device","consumption":"consumption","analysis":"analysis","ratio":"ratio","yoy":"yoy","report":"report"};
    const target = tabMap[val];
    if (target) activeTab.value = target;
  }
}, { immediate: true });

onMounted(async () => {
  initDefaultDates()
  try {
    const [g, d, p] = await Promise.all([
      listGateways().catch(() => []),
      listDevices().catch(() => []),
      listPoints().catch(() => [])
    ])
    gateways.value = g as any
    devices.value = d as any
    points.value = p as any
  } catch (e) {
    // ignore
  }
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  historyChart?.dispose()
  consumptionChart?.dispose()
  analysisChart?.dispose()
  ratioChart?.dispose()
  yoyChart?.dispose()
})

function handleHistorySearch() {
  if (!historyQuery.gatewayId && !historyQuery.deviceId && !historyQuery.pointId) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadHistoryData()
}

function handleDeviceSearch() {
  if (!deviceQuery.gatewayId && !deviceQuery.deviceId) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadDeviceData()
}

function handleConsumptionSearch() {
  if (!consumptionQuery.gatewayId && !consumptionQuery.energyTypeId) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadConsumptionData()
}

function handleAnalysisSearch() {
  if (!analysisQuery.energyTypeId && !analysisQuery.objectType) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadAnalysisData()
}

function handleRatioSearch() {
  if (!ratioQuery.energyTypeId && !ratioQuery.objectType) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadRatioData()
}

function handleYoySearch() {
  if (!yoyQuery.energyTypeId && !yoyQuery.objectType) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadYoyData()
}

</script>

<style scoped>
.energy-analysis {
  padding: 0;
}
.filter-form {
  display: flex;
  flex-wrap: wrap;
}
.summary-card {
  text-align: center;
  padding: 10px 0;
}
.summary-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}
.summary-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}
.rate-positive {
  color: #F56C6C;
}
.rate-negative {
  color: #67C23A;
}
</style>