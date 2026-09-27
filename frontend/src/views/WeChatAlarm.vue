<template>
  <div class="wechat-alarm">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">

      <!-- ========== Tab 1: 消息统计 ========== -->
      <el-tab-pane label="消息统计" name="stats">
        <el-card>
          <template #header>消息统计</template>
          <el-form :inline="true" :model="statsQuery" class="filter-form">
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="statsDateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width:260px"
              />
            </el-form-item>
            <el-form-item label="时间区间">
              <el-slider
                v-model="statsSliderRange"
                range
                :min="0"
                :max="100"
                style="width:300px"
                @change="onStatsSliderChange"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleStatsSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">关注用户数</div>
              <div class="summary-value">{{ statsUserCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">设备总数</div>
              <div class="summary-value">{{ statsDeviceCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">消息发送总数</div>
              <div class="summary-value">{{ statsMessageCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">总消息数</div>
              <div class="summary-value">{{ statsResult.total || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">已读消息</div>
              <div class="summary-value">{{ statsResult.read || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">未读消息</div>
              <div class="summary-value">{{ statsResult.unread || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="summary-card">
              <div class="summary-label">告警消息</div>
              <div class="summary-value">{{ statsResult.alarm || 0 }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top:16px">
          <template #header>每日消息趋势</template>
          <div v-loading="statsLoading" ref="statsChartRef" style="height:400px"></div>
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 2: 关注用户 ========== -->
      <el-tab-pane label="关注用户" name="users">
        <el-card>
          <template #header>关注用户</template>
          <el-form :inline="true" :model="userQuery" class="filter-form">
            <el-form-item label="微信昵称">
              <el-input v-model="userQuery.keyword" placeholder="搜索昵称" clearable style="width:200px" @keyup.enter="onUserSearch" />
            </el-form-item>
            <el-form-item label="分组">
              <el-input v-model="userQuery.group" placeholder="搜索分组" clearable style="width:200px" @keyup.enter="onUserSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUserSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="exportUsers">
                <el-icon><Download /></el-icon>导出Excel
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="users" border stripe v-loading="usersLoading" style="margin-top:16px">
            <el-table-column prop="nickname" label="微信昵称" />
            <el-table-column prop="openid" label="openid" min-width="220" show-overflow-tooltip />
            <el-table-column prop="subscribeTime" label="订阅时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'SUBSCRIBED' ? 'success' : 'info'">
                  {{ row.status === 'SUBSCRIBED' ? '关注' : '取关' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240">
              <template #default="{ row }">
                <el-button size="small" @click="sendMessage(row)">发消息</el-button>
                <el-button size="small" type="danger" @click="blockUser(row)">拉黑</el-button>
                <el-link type="warning" @click="banDevice(row)" style="margin-left:8px">禁止</el-link>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            style="margin-top:16px; justify-content:flex-end"
            v-model:current-page="userQuery.page"
            v-model:page-size="userQuery.size"
            :total="userTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadUsers"
            @current-change="loadUsers"
          />
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 3: 告警历史 ========== -->
      <el-tab-pane label="告警历史" name="history">
        <el-card>
          <template #header>告警历史</template>
          <el-form :inline="true" :model="alarmQuery" class="filter-form">
            <el-form-item label="设备ID">
              <el-input v-model="alarmQuery.deviceId" placeholder="设备ID" clearable style="width:160px" @keyup.enter="onAlarmSearch" />
            </el-form-item>
            <el-form-item label="设备名称">
              <el-input v-model="alarmQuery.deviceName" placeholder="设备名称" clearable style="width:160px" @keyup.enter="onAlarmSearch" />
            </el-form-item>
            <el-form-item label="告警级别">
              <el-select v-model="alarmQuery.level" placeholder="告警级别" clearable style="width:120px">
                <el-option label="紧急" value="紧急" />
                <el-option label="重要" value="重要" />
                <el-option label="次要" value="次要" />
                <el-option label="警告" value="警告" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="alarmDateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width:260px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAlarmHistorySearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="exportAlarms">
                <el-icon><Download /></el-icon>导出Excel
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="alarms" border stripe v-loading="alarmsLoading" style="margin-top:16px">
            <el-table-column prop="title" label="告警标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="deviceName" label="设备名称" width="150" />
            <el-table-column prop="level" label="告警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="alarmLevelType(row.level)">{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sendTime" label="发送时间" width="180" />
            <el-table-column prop="status" label="消息状态" width="100">
              <template #default="{ row }">
                <el-tag :type="msgStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="pushResult" label="推送结果" min-width="160" show-overflow-tooltip />
          </el-table>

          <el-pagination
            style="margin-top:16px; justify-content:flex-end"
            v-model:current-page="alarmQuery.page"
            v-model:page-size="alarmQuery.size"
            :total="alarmTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadAlarms"
            @current-change="loadAlarms"
          />
        </el-card>
      </el-tab-pane>

      <!-- ========== Tab 4: 设备黑名单 ========== -->
      <el-tab-pane label="设备黑名单" name="deviceBlacklist">
        <el-card>
          <template #header>设备黑名单</template>
          <div class="toolbar">
            <el-button type="primary" @click="openDeviceBlackDialog()">
              <el-icon><Plus /></el-icon>添加黑名单
            </el-button>
          </div>

          <el-table :data="deviceBlacklist" border stripe v-loading="deviceBlackLoading" style="margin-top:16px">
            <el-table-column prop="deviceName" label="设备名称" width="160" />
            <el-table-column prop="deviceCode" label="设备编码" width="160" />
            <el-table-column prop="area" label="区域" width="160" />
            <el-table-column prop="blockTime" label="拉黑时间" width="180" />
            <el-table-column prop="reason" label="拉黑原因" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="warning" @click="removeDeviceBlack(row)">恢复</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- Device Blacklist Dialog -->
        <el-dialog v-model="deviceBlackVisible" title="添加设备黑名单" width="500px">
          <el-form :model="deviceBlackForm" label-width="90px">
            <el-form-item label="设备">
              <el-select v-model="deviceBlackForm.deviceId" placeholder="选择设备" filterable style="width:100%">
                <el-option v-for="d in deviceOptions" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="拉黑原因">
              <el-input v-model="deviceBlackForm.reason" type="textarea" :rows="3" placeholder="请输入拉黑原因" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="deviceBlackVisible = false">取消</el-button>
            <el-button type="primary" @click="saveDeviceBlack">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- ========== Tab 5: 用户黑名单 ========== -->
      <el-tab-pane label="用户黑名单" name="userBlacklist">
        <el-card>
          <template #header>用户黑名单</template>
          <div class="toolbar">
            <el-button type="primary" @click="openUserBlackDialog()">
              <el-icon><Plus /></el-icon>添加黑名单
            </el-button>
          </div>

          <el-table :data="userBlacklist" border stripe v-loading="userBlackLoading" style="margin-top:16px">
            <el-table-column prop="nickname" label="微信昵称" width="180" />
            <el-table-column prop="openid" label="openid" min-width="220" show-overflow-tooltip />
            <el-table-column prop="blockTime" label="拉黑时间" width="180" />
            <el-table-column prop="reason" label="拉黑原因" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="warning" @click="removeUserBlack(row)">恢复</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- User Blacklist Dialog -->
        <el-dialog v-model="userBlackVisible" title="添加用户黑名单" width="500px">
          <el-form :model="userBlackForm" label-width="90px">
            <el-form-item label="openid">
              <el-input v-model="userBlackForm.openid" placeholder="请输入openid" />
            </el-form-item>
            <el-form-item label="微信昵称">
              <el-input v-model="userBlackForm.nickname" placeholder="请输入微信昵称" />
            </el-form-item>
            <el-form-item label="拉黑原因">
              <el-input v-model="userBlackForm.reason" type="textarea" :rows="3" placeholder="请输入拉黑原因" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="userBlackVisible = false">取消</el-button>
            <el-button type="primary" @click="saveUserBlack">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Download, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
  const activeTab = ref('stats')

// ==================== Tab 1: 消息统计 ====================
const statsChartRef = ref<HTMLElement>()
const statsLoading = ref(false)
let statsChart: echarts.ECharts | null = null
const statsDateRange = ref<[string, string] | []>([])
const statsQuery = reactive({ startDate: '', endDate: '' })
const statsResult = ref<any>({ total: 0, read: 0, unread: 0, alarm: 0, daily: [] })
const statsSliderRange = ref<[number, number]>([0, 100])
const statsUserCount = ref(0)
const statsDeviceCount = ref(0)
const statsMessageCount = ref(0)

function onStatsSliderChange() {
  const today = new Date()
  const fmt = (d: Date) => {
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return y + '-' + m + '-' + day
  }
  const low = statsSliderRange.value[0]
  const high = statsSliderRange.value[1]
  const start = new Date(today)
  start.setDate(start.getDate() - high)
  const end = new Date(today)
  end.setDate(end.getDate() - low)
  statsDateRange.value = [fmt(start), fmt(end)]
  loadStats()
}

async function loadStatsCounts() {
  try {
    const [userRes, deviceRes] = await Promise.all([
      request.get('/wechat/users/count') as any,
      request.get('/devices', { params: { page: 1, size: 1 } }) as any
    ])
    statsUserCount.value = (typeof userRes === 'number') ? userRes : (userRes?.total || userRes?.count || 0)
    statsDeviceCount.value = deviceRes?.total || deviceRes?.count || (Array.isArray(deviceRes) ? deviceRes.length : 0)
  } catch (e) {
    statsUserCount.value = 0
    statsDeviceCount.value = 0
  }
}

async function loadStats() {
  if (statsDateRange.value && statsDateRange.value.length === 2) {
    statsQuery.startDate = statsDateRange.value[0]
    statsQuery.endDate = statsDateRange.value[1]
  } else {
    statsQuery.startDate = ''
    statsQuery.endDate = ''
  }
  statsLoading.value = true
  loadStatsCounts()
  try {
    const res = await request.get('/wechat/messages/stats', {
      params: { startDate: statsQuery.startDate, endDate: statsQuery.endDate }
    }) as any
    statsResult.value = res || { total: 0, read: 0, unread: 0, alarm: 0, daily: [] }
    statsMessageCount.value = statsResult.value.total || 0
    initStatsChart()
  } catch (e) {
    statsResult.value = { total: 0, read: 0, unread: 0, alarm: 0, daily: [] }
    statsMessageCount.value = 0
    initStatsChart()
  } finally {
    statsLoading.value = false
  }
}

function initStatsChart() {
  nextTick(() => {
    if (!statsChartRef.value) return
    statsChart?.dispose()
    statsChart = echarts.init(statsChartRef.value)
    const daily = statsResult.value.daily || []
    statsChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: daily.map((d: any) => d.date) },
      yAxis: { type: 'value', name: '消息数' },
      series: [{
        name: '消息数',
        type: 'bar',
        data: daily.map((d: any) => Number(d.count)),
        itemStyle: { color: '#409EFF' },
        barWidth: '50%'
      }]
    })
  })
}

// ==================== Tab 2: 关注用户 ====================
const usersLoading = ref(false)
const users = ref<any[]>([])
const userTotal = ref(0)
const userQuery = reactive({ page: 1, size: 20, keyword: '', group: '' })

async function loadUsers() {
  usersLoading.value = true
  try {
    const res = await request.get('/wechat/users', {
      params: { page: userQuery.page, size: userQuery.size, keyword: userQuery.keyword, group: userQuery.group }
    }) as any
    users.value = res?.items || res?.records || []
    userTotal.value = res?.total || 0
  } catch (e) {
    users.value = []
    userTotal.value = 0
  } finally {
    usersLoading.value = false
  }
}

function onUserSearch() {
  userQuery.page = 1
  loadUsers()
}

function sendMessage(row: any) {
  ElMessage.info('发消息给 ' + (row.nickname || row.openid))
}

async function blockUser(row: any) {
  try {
    await ElMessageBox.confirm('确认拉黑用户 ' + (row.nickname || row.openid) + ' ？', '提示', { type: 'warning' })
    await request.post('/wechat/user-blacklist', { openid: row.openid, nickname: row.nickname, reason: '手动拉黑' })
    ElMessage.success('已拉黑')
    loadUsers()
  } catch (e) {
    // cancelled or failed
  }
}

function exportUsers() {
  const params = new URLSearchParams()
  if (userQuery.keyword) params.append('keyword', userQuery.keyword)
  if (userQuery.group) params.append('group', userQuery.group)
  window.open('/wechat/users/export?' + params.toString())
}

async function banDevice(row: any) {
  try {
    await ElMessageBox.confirm('确认禁止关注该设备？禁止后该设备相关报警信息将不会发送给用户', '提示', { type: 'warning' })
    await request.post('/wechat/device-blacklist', { deviceId: row.deviceId, reason: '手动禁止' })
    ElMessage.success('已禁止')
    loadUsers()
  } catch (e) {
    // cancelled or failed
  }
}

// ==================== Tab 3: 告警历史 ====================
const alarmsLoading = ref(false)
const alarms = ref<any[]>([])
const alarmTotal = ref(0)
const alarmDateRange = ref<[string, string] | []>([])
const alarmQuery = reactive({
  page: 1,
  size: 20,
  deviceId: '',
  deviceName: '',
  level: '',
  startDate: '',
  endDate: ''
})

async function loadAlarms() {
  if (alarmDateRange.value && alarmDateRange.value.length === 2) {
    alarmQuery.startDate = alarmDateRange.value[0]
    alarmQuery.endDate = alarmDateRange.value[1]
  } else {
    alarmQuery.startDate = ''
    alarmQuery.endDate = ''
  }
  alarmsLoading.value = true
  try {
    const res = await request.get('/wechat/alarms/history', {
      params: {
        page: alarmQuery.page,
        size: alarmQuery.size,
        deviceId: alarmQuery.deviceId,
        deviceName: alarmQuery.deviceName,
        level: alarmQuery.level,
        startDate: alarmQuery.startDate,
        endDate: alarmQuery.endDate
      }
    }) as any
    alarms.value = res?.items || res?.records || []
    alarmTotal.value = res?.total || 0
  } catch (e) {
    alarms.value = []
    alarmTotal.value = 0
  } finally {
    alarmsLoading.value = false
  }
}

function onAlarmSearch() {
  alarmQuery.page = 1
  loadAlarms()
}

function exportAlarms() {
  const params = new URLSearchParams()
  if (alarmQuery.deviceId) params.append('deviceId', alarmQuery.deviceId)
  if (alarmQuery.deviceName) params.append('deviceName', alarmQuery.deviceName)
  if (alarmQuery.level) params.append('level', alarmQuery.level)
  if (alarmQuery.startDate) params.append('startDate', alarmQuery.startDate)
  if (alarmQuery.endDate) params.append('endDate', alarmQuery.endDate)
  window.open('/api/v1/wechat/alarms/history/export?' + params.toString())
}

function alarmLevelType(level: string) {
  return ({ '紧急': 'danger', '重要': 'warning', '次要': 'info', '警告': 'primary' } as any)[level] || ''
}

function msgStatusType(status: string) {
  return ({ '已发送': 'success', '已读': 'primary', '失败': 'danger' } as any)[status] || 'info'
}

// ==================== Tab 4: 设备黑名单 ====================
const deviceBlackLoading = ref(false)
const deviceBlacklist = ref<any[]>([])
const deviceOptions = ref<any[]>([])
const deviceBlackVisible = ref(false)
const deviceBlackForm = reactive({ deviceId: undefined as any, reason: '' })

async function loadDeviceBlacklist() {
  deviceBlackLoading.value = true
  try {
    const res = await request.get('/wechat/device-blacklist') as any
    deviceBlacklist.value = Array.isArray(res) ? res : (res?.items || res?.records || [])
  } catch (e) {
    deviceBlacklist.value = []
  } finally {
    deviceBlackLoading.value = false
  }
}

async function loadDeviceOptions() {
  try {
    const res = await request.get('/devices', { params: { page: 1, size: 1000 } }) as any
    deviceOptions.value = res?.items || res?.records || (Array.isArray(res) ? res : [])
  } catch (e) {
    deviceOptions.value = []
  }
}

function openDeviceBlackDialog() {
  deviceBlackForm.deviceId = undefined
  deviceBlackForm.reason = ''
  deviceBlackVisible.value = true
  loadDeviceOptions()
}

async function saveDeviceBlack() {
  if (!deviceBlackForm.deviceId) {
    ElMessage.warning('请选择设备')
    return
  }
  if (!deviceBlackForm.reason) {
    ElMessage.warning('请输入拉黑原因')
    return
  }
  await request.post('/wechat/device-blacklist', {
    deviceId: deviceBlackForm.deviceId,
    reason: deviceBlackForm.reason
  })
  ElMessage.success('添加成功')
  deviceBlackVisible.value = false
  loadDeviceBlacklist()
}

async function removeDeviceBlack(row: any) {
  try {
    await ElMessageBox.confirm('确认恢复该设备？', '提示', { type: 'warning' })
    await request.delete('/wechat/device-blacklist/' + row.id)
    ElMessage.success('已恢复')
    loadDeviceBlacklist()
  } catch (e) {
    // cancelled or failed
  }
}

// ==================== Tab 5: 用户黑名单 ====================
const userBlackLoading = ref(false)
const userBlacklist = ref<any[]>([])
const userBlackVisible = ref(false)
const userBlackForm = reactive({ openid: '', nickname: '', reason: '' })

async function loadUserBlacklist() {
  userBlackLoading.value = true
  try {
    const res = await request.get('/wechat/user-blacklist') as any
    userBlacklist.value = Array.isArray(res) ? res : (res?.items || res?.records || [])
  } catch (e) {
    userBlacklist.value = []
  } finally {
    userBlackLoading.value = false
  }
}

function openUserBlackDialog() {
  userBlackForm.openid = ''
  userBlackForm.nickname = ''
  userBlackForm.reason = ''
  userBlackVisible.value = true
}

async function saveUserBlack() {
  if (!userBlackForm.openid) {
    ElMessage.warning('请输入openid')
    return
  }
  if (!userBlackForm.nickname) {
    ElMessage.warning('请输入微信昵称')
    return
  }
  if (!userBlackForm.reason) {
    ElMessage.warning('请输入拉黑原因')
    return
  }
  await request.post('/wechat/user-blacklist', {
    openid: userBlackForm.openid,
    nickname: userBlackForm.nickname,
    reason: userBlackForm.reason
  })
  ElMessage.success('添加成功')
  userBlackVisible.value = false
  loadUserBlacklist()
}

async function removeUserBlack(row: any) {
  try {
    await ElMessageBox.confirm('确认恢复该用户？', '提示', { type: 'warning' })
    await request.delete('/wechat/user-blacklist/' + row.id)
    ElMessage.success('已恢复')
    loadUserBlacklist()
  } catch (e) {
    // cancelled or failed
  }
}

// ==================== Common ====================
function handleTabChange() {
  nextTick(() => {
    statsChart?.resize()
  })
}

function handleResize() {
  statsChart?.resize()
}

// ==================== Lifecycle ====================

watch(() => route.query.tab, (val) => {
  if (typeof val === "string") {
    const tabMap: Record<string, string> = {"stats":"stats","users":"users","history":"history","device-bl":"deviceBlacklist","user-bl":"userBlacklist"};
    const target = tabMap[val];
    if (target) activeTab.value = target;
  }
}, { immediate: true });

function handleStatsSearch() {
  if (!statsQuery.startDate && !statsQuery.endDate) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadStats()
}

function handleUserSearch() {
  if (!userQuery.keyword && !userQuery.group) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  onUserSearch()
}

function handleAlarmHistorySearch() {
  if (!alarmQuery.deviceId && !alarmQuery.deviceName) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  onAlarmSearch()
}

onMounted(() => {
  loadStats()
  loadUsers()
  loadAlarms()
  loadDeviceBlacklist()
  loadUserBlacklist()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  statsChart?.dispose()
})
</script>

<style scoped>
.wechat-alarm {
  padding: 0;
}
.filter-form {
  display: flex;
  flex-wrap: wrap;
}
.toolbar {
  margin-bottom: 16px;
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
</style>