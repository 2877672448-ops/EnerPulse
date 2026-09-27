<template>
  <div class="device-management">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- ============ Tab 1: 网关管理 ============ -->
      <el-tab-pane label="网关管理" name="gateway">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>网关管理</span>
              <el-button type="primary" :icon="Plus" @click="openGatewayDialog()">添加</el-button>
            </div>
          </template>
          <el-table :data="gateways" v-loading="gatewayLoading" stripe border>
            <el-table-column prop="name" label="网关名称" min-width="120" />
            <el-table-column prop="topic" label="主题" min-width="140" />
            <el-table-column prop="gatewayId" label="网关ID" min-width="120" />
            <el-table-column prop="serverHost" label="IP地址" min-width="120" />
            <el-table-column prop="serverPort" label="端口" width="80" />
            <el-table-column prop="reportInterval" label="上报周期" width="100">
              <template #default="{ row }">{{ row.reportInterval }}s</template>
            </el-table-column>
            <el-table-column prop="valueType" label="值类型" width="90">
              <template #default="{ row }">{{ valueTypeText(row.valueType) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ONLINE' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="210">
              <template #default="{ row }">
                <el-link type="primary" :disabled="row.status !== 'ONLINE' || syncLoading" @click="syncPoints(row)">同步点名</el-link>
                <el-divider direction="vertical" />
                <el-link type="primary" @click="openGatewayDialog(row)">编辑</el-link>
                <el-divider direction="vertical" />
                <el-link type="danger" @click="deleteGateway(row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-dialog v-model="gatewayDialog.visible" :title="gatewayDialog.title" width="600px" @close="resetGatewayForm">
          <el-form :model="gatewayForm" label-width="100px">
            <el-form-item label="网关名称" required>
              <el-input v-model="gatewayForm.name" placeholder="请输入网关名称" />
            </el-form-item>
            <el-form-item label="主题" required>
              <el-input v-model="gatewayForm.topic" placeholder="MQTT主题，不同网关不能重复" />
            </el-form-item>
            <el-form-item label="网关ID">
              <el-input v-model="gatewayForm.gatewayId" disabled placeholder="自动生成" />
            </el-form-item>
            <el-form-item label="IP地址">
              <el-input v-model="gatewayForm.serverHost" placeholder="如: 121.41.12.110" />
            </el-form-item>
            <el-form-item label="端口">
              <el-input-number v-model="gatewayForm.serverPort" :min="1" :max="65535" disabled />
            </el-form-item>
            <el-form-item label="上报周期/秒">
              <el-input-number v-model="gatewayForm.reportInterval" :min="1" />
            </el-form-item>
            <el-form-item label="值类型" required>
              <el-select v-model="gatewayForm.valueType" placeholder="请选择值类型">
                <el-option v-for="opt in valueTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="gatewayForm.status">
                <el-option label="在线" value="ONLINE" />
                <el-option label="离线" value="OFFLINE" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="gatewayDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="saveGateway">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- ============ Tab 2: 点名管理 ============ -->
      <el-tab-pane label="点名管理" name="point">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>点名管理</span>
              <div class="header-actions">
                <el-button type="primary" :icon="Plus" @click="openPointDialog()">添加</el-button>
                <el-button :icon="Refresh" :loading="batchSyncLoading" @click="batchSyncPoints">批量同步</el-button>
              </div>
            </div>
          </template>
          <el-alert
            class="point-tip"
            type="info"
            :closable="false"
            show-icon
            title="瞬时量可使用历史数据、设备数据；累计量可使用历史数据、设备数据、能源消耗、能耗分析、能耗占比、同比分析"
          />
          <el-table :data="points" v-loading="pointLoading" stripe border>
            <el-table-column prop="pointCode" label="测点编号" min-width="120" />
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="gatewayName" label="网关" min-width="110" />
            <el-table-column prop="deviceName" label="设备" min-width="110" />
            <el-table-column prop="energyTypeName" label="能耗类型" width="100" />
            <el-table-column prop="energyItemName" label="能耗分项" width="120" />
            <el-table-column prop="valueType" label="值类型" width="90">
              <template #default="{ row }">{{ valueTypeText(row.valueType) }}</template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="90" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="130">
              <template #default="{ row }">
                <el-link type="primary" @click="openPointDialog(row)">编辑</el-link>
                <el-divider direction="vertical" />
                <el-link type="danger" @click="deletePoint(row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-dialog v-model="pointDialog.visible" :title="pointDialog.title" width="640px" @close="resetPointForm">
          <el-form :model="pointForm" label-width="100px">
            <el-form-item label="测点编号" required>
              <el-input v-model="pointForm.pointCode" placeholder="请输入测点编号" />
            </el-form-item>
            <el-form-item label="名称" required>
              <el-input v-model="pointForm.name" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item label="所属网关" required>
              <el-select v-model="pointForm.gatewayId" placeholder="请选择网关" @change="onPointGatewayChange">
                <el-option v-for="g in gateways" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="所属设备" required>
              <el-select v-model="pointForm.deviceId" placeholder="请选择设备">
                <el-option v-for="d in pointDeviceOptions" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="能耗类型">
              <el-select v-model="pointForm.energyTypeId" placeholder="请选择能耗类型" clearable>
                <el-option v-for="opt in energyTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="能耗分项">
              <el-select v-model="pointForm.energyItemId" placeholder="请选择能耗分项" clearable>
                <el-option v-for="opt in energyItemOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="值类型" required>
              <el-select v-model="pointForm.valueType" placeholder="请选择值类型">
                <el-option v-for="opt in valueTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="数值单位">
              <el-select v-model="pointForm.unit" placeholder="请选择单位" clearable>
                <el-option v-for="opt in unitOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="pointForm.status">
                <el-option label="启用" value="ACTIVE" />
                <el-option label="停用" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="pointDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="savePoint">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- ============ Tab 3: 设备管理 ============ -->
      <el-tab-pane label="设备管理" name="device">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>设备管理</span>
              <el-button type="primary" :icon="Plus" @click="openDeviceDialog()">添加</el-button>
            </div>
          </template>
          <el-table :data="devices" v-loading="deviceLoading" stripe border>
            <el-table-column prop="deviceCode" label="设备编号" min-width="120" />
            <el-table-column prop="name" label="设备名称" min-width="130" />
            <el-table-column prop="energyTypeName" label="能耗类型" width="100" />
            <el-table-column prop="buildingName" label="建筑" min-width="110" />
            <el-table-column prop="floorName" label="楼层" min-width="100" />
            <el-table-column prop="locationName" label="位置" min-width="110" />
            <el-table-column prop="gatewayName" label="网关" min-width="110" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ONLINE' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="130">
              <template #default="{ row }">
                <el-link type="primary" @click="openDeviceDialog(row)">编辑</el-link>
                <el-divider direction="vertical" />
                <el-link type="danger" @click="deleteDevice(row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-dialog v-model="deviceDialog.visible" :title="deviceDialog.title" width="640px" @close="resetDeviceForm">
          <el-form :model="deviceForm" label-width="100px">
            <el-form-item label="设备编号" required>
              <el-input v-model="deviceForm.deviceCode" placeholder="请输入设备编号" />
            </el-form-item>
            <el-form-item label="设备名称" required>
              <el-input v-model="deviceForm.name" placeholder="请输入设备名称" />
            </el-form-item>
            <el-form-item label="能耗类型">
              <el-select v-model="deviceForm.energyTypeId" placeholder="请选择能耗类型（配合字典管理使用）" clearable>
                <el-option v-for="opt in energyTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="所属网关">
              <el-select v-model="deviceForm.gatewayId" placeholder="请选择网关" clearable>
                <el-option v-for="g in gateways" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="建筑">
              <el-select v-model="deviceForm.buildingId" placeholder="请选择建筑" clearable @change="onBuildingChange">
                <el-option v-for="b in buildingOptions" :key="b.id" :label="b.name" :value="b.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="楼层">
              <el-select v-model="deviceForm.floorId" placeholder="请选择楼层" clearable @change="onFloorChange">
                <el-option v-for="f in floorOptions" :key="f.id" :label="f.name" :value="f.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="位置">
              <el-select v-model="deviceForm.locationId" placeholder="请选择位置" clearable>
                <el-option v-for="l in locationOptions" :key="l.id" :label="l.name" :value="l.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备类型">
              <el-input v-model="deviceForm.deviceType" placeholder="请输入设备类型" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="deviceForm.status">
                <el-option label="在线" value="ONLINE" />
                <el-option label="离线" value="OFFLINE" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="deviceDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="saveDevice">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
  const activeTab = ref('gateway')

// ---- 共享字典选项 ----
const valueTypeOptions = [
  { label: '瞬时量', value: 'INSTANTANEOUS' },
  { label: '累计量', value: 'CUMULATIVE' }
]
const energyTypeOptions = [
  { label: '无能耗类型', value: 0 },
  { label: '电表', value: 1 },
  { label: '水表', value: 2 },
  { label: '热量表', value: 3 },
  { label: '冷量表', value: 4 },
  { label: '气', value: 5 }
]
const energyItemOptions = [
  { label: '正向有功电能', value: 1 },
  { label: '相电压Ua', value: 2 },
  { label: '相电压Ub', value: 3 },
  { label: '相电压Uc', value: 4 },
  { label: '累计用水量', value: 5 },
  { label: '瞬时用水量', value: 6 }
]
const unitOptions = [
  { label: '无', value: '' },
  { label: 'V(伏)', value: 'V' },
  { label: 'A(安)', value: 'A' },
  { label: 'kW(千瓦)', value: 'kW' },
  { label: 'kWh(千瓦时)', value: 'kWh' },
  { label: 'm³(立方米)', value: 'm³' },
  { label: 'm³/h(立方米/时)', value: 'm³/h' },
  { label: 't(吨)', value: 't' },
  { label: 'GJ(吉焦)', value: 'GJ' }
]

function valueTypeText(val: string) {
  return val === 'INSTANTANEOUS' ? '瞬时量' : val === 'CUMULATIVE' ? '累计量' : val
}

// ===================== 网关管理 =====================
const gateways = ref<any[]>([])
const gatewayLoading = ref(false)
const syncLoading = ref(false)
const gatewayDialog = reactive({ visible: false, title: '' })
const defaultGatewayForm = () => ({
  id: undefined as any,
  name: '',
  topic: '',
  gatewayId: '',
  serverHost: '',
  serverPort: 1883,
  reportInterval: 60,
  valueType: 'INSTANTANEOUS',
  status: 'ONLINE'
})
const gatewayForm = reactive<any>(defaultGatewayForm())

async function loadGateways() {
  gatewayLoading.value = true
  try {
    const data = await request.get('/gateways')
    gateways.value = (data as any) || []
  } finally {
    gatewayLoading.value = false
  }
}

function openGatewayDialog(row?: any) {
  if (row) {
    Object.assign(gatewayForm, defaultGatewayForm(), row)
    gatewayDialog.title = '编辑网关'
  } else {
    Object.assign(gatewayForm, defaultGatewayForm())
    gatewayDialog.title = '添加网关'
  }
  gatewayDialog.visible = true
}

function resetGatewayForm() {
  Object.assign(gatewayForm, defaultGatewayForm())
}

async function saveGateway() {
  if (!gatewayForm.name) return ElMessage.warning('请输入网关名称')
  if (!gatewayForm.topic) return ElMessage.warning('请输入主题')
  if (!gatewayForm.valueType) return ElMessage.warning('网关值类型不能为空')
  try {
    if (gatewayForm.id) {
      await request.put(`/gateways/${gatewayForm.id}`, gatewayForm)
    } else {
      await request.post('/gateways', gatewayForm)
    }
    ElMessage.success('保存成功')
    gatewayDialog.visible = false
    loadGateways()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function deleteGateway(row: any) {
  try {
    await ElMessageBox.confirm('确认删除该网关吗？', '提示', { type: 'warning' })
    await request.delete(`/gateways/${row.id}`)
    ElMessage.success('删除成功')
    loadGateways()
  } catch (e) {
    // 用户取消或请求失败
  }
}

async function syncPoints(row: any) {
  syncLoading.value = true
  try {
    const data: any = await request.post('/points/sync', { gatewayId: row.gatewayId })
    const count = Array.isArray(data) ? data.length : typeof data === 'number' ? data : (data?.count ?? 0)
    ElMessage.success(`同步成功，共添加${count}个测点`)
    loadPoints()
  } catch (e) {
    ElMessage.error('同步失败')
  } finally {
    syncLoading.value = false
  }
}

// ===================== 点名管理 =====================
const points = ref<any[]>([])
const pointLoading = ref(false)
const batchSyncLoading = ref(false)
const pointDialog = reactive({ visible: false, title: '' })
const defaultPointForm = () => ({
  id: undefined as any,
  pointCode: '',
  name: '',
  gatewayId: undefined as any,
  deviceId: undefined as any,
  energyTypeId: undefined as any,
  energyItemId: undefined as any,
  valueType: 'INSTANTANEOUS',
  unit: '',
  status: 'ACTIVE'
})
const pointForm = reactive<any>(defaultPointForm())

const pointDeviceOptions = computed(() => {
  if (pointForm.gatewayId != null) {
    return devices.value.filter((d) => d.gatewayId === pointForm.gatewayId)
  }
  return devices.value
})

async function loadPoints() {
  pointLoading.value = true
  try {
    const data = await request.get('/points')
    points.value = (data as any) || []
  } finally {
    pointLoading.value = false
  }
}

function openPointDialog(row?: any) {
  if (row) {
    Object.assign(pointForm, defaultPointForm(), row)
    pointDialog.title = '编辑测点'
  } else {
    Object.assign(pointForm, defaultPointForm())
    pointDialog.title = '添加测点'
  }
  pointDialog.visible = true
}

function resetPointForm() {
  Object.assign(pointForm, defaultPointForm())
}

function onPointGatewayChange() {
  pointForm.deviceId = undefined
}

async function savePoint() {
  if (!pointForm.pointCode) return ElMessage.warning('请输入测点编号')
  if (!pointForm.name) return ElMessage.warning('请输入名称')
  if (pointForm.gatewayId == null) return ElMessage.warning('请选择所属网关')
  if (pointForm.deviceId == null) return ElMessage.warning('请选择所属设备')
  if (!pointForm.valueType) return ElMessage.warning('请选择值类型')
  try {
    if (pointForm.id) {
      await request.put(`/points/${pointForm.id}`, pointForm)
    } else {
      await request.post('/points', pointForm)
    }
    ElMessage.success('保存成功')
    pointDialog.visible = false
    loadPoints()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function deletePoint(row: any) {
  try {
    await ElMessageBox.confirm('确认删除该测点吗？', '提示', { type: 'warning' })
    await request.delete(`/points/${row.id}`)
    ElMessage.success('删除成功')
    loadPoints()
  } catch (e) {
    // 用户取消或请求失败
  }
}

async function batchSyncPoints() {
  batchSyncLoading.value = true
  try {
    const data: any = await request.post('/points/sync', {})
    const count = Array.isArray(data) ? data.length : typeof data === 'number' ? data : (data?.count ?? 0)
    ElMessage.success(`同步成功，共添加${count}个测点`)
    loadPoints()
  } catch (e) {
    ElMessage.error('同步失败')
  } finally {
    batchSyncLoading.value = false
  }
}

// ===================== 设备管理 =====================
const devices = ref<any[]>([])
const deviceLoading = ref(false)
const areas = ref<any[]>([])
const deviceDialog = reactive({ visible: false, title: '' })
const defaultDeviceForm = () => ({
  id: undefined as any,
  deviceCode: '',
  name: '',
  energyTypeId: undefined as any,
  gatewayId: undefined as any,
  buildingId: undefined as any,
  floorId: undefined as any,
  locationId: undefined as any,
  deviceType: '',
  status: 'ONLINE'
})
const deviceForm = reactive<any>(defaultDeviceForm())

const buildingOptions = computed(() => areas.value.filter((a) => a.type === 'BUILDING'))
const floorOptions = computed(() =>
  areas.value.filter((a) => a.type === 'FLOOR' && a.parentId === deviceForm.buildingId)
)
const locationOptions = computed(() =>
  areas.value.filter((a) => a.type === 'LOCATION' && a.parentId === deviceForm.floorId)
)

async function loadDevices() {
  deviceLoading.value = true
  try {
    const data = await request.get('/devices')
    devices.value = (data as any) || []
  } finally {
    deviceLoading.value = false
  }
}

async function loadAreas() {
  try {
    const data = await request.get('/areas')
    areas.value = (data as any) || []
  } catch (e) {
    // 忽略区域加载失败
  }
}

function openDeviceDialog(row?: any) {
  if (row) {
    Object.assign(deviceForm, defaultDeviceForm(), row)
    deviceDialog.title = '编辑设备'
  } else {
    Object.assign(deviceForm, defaultDeviceForm())
    deviceDialog.title = '添加设备'
  }
  deviceDialog.visible = true
}

function resetDeviceForm() {
  Object.assign(deviceForm, defaultDeviceForm())
}

function onBuildingChange() {
  deviceForm.floorId = undefined
  deviceForm.locationId = undefined
}

function onFloorChange() {
  deviceForm.locationId = undefined
}

async function saveDevice() {
  if (!deviceForm.deviceCode) return ElMessage.warning('请输入设备编号')
  if (!deviceForm.name) return ElMessage.warning('请输入设备名称')
  try {
    if (deviceForm.id) {
      await request.put(`/devices/${deviceForm.id}`, deviceForm)
    } else {
      await request.post('/devices', deviceForm)
    }
    ElMessage.success('保存成功')
    deviceDialog.visible = false
    loadDevices()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function deleteDevice(row: any) {
  try {
    await ElMessageBox.confirm('确认删除该设备吗？', '提示', { type: 'warning' })
    await request.delete(`/devices/${row.id}`)
    ElMessage.success('删除成功')
    loadDevices()
  } catch (e) {
    // 用户取消或请求失败
  }
}


watch(() => route.query.tab, (val) => {
  if (typeof val === "string") {
    const tabMap: Record<string, string> = {"gateway":"gateway","point":"point","device":"device"};
    const target = tabMap[val];
    if (target) activeTab.value = target;
  }
}, { immediate: true });

onMounted(() => {
  loadGateways()
  loadPoints()
  loadDevices()
  loadAreas()
})
</script>

<style scoped>
.device-management {
  padding: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-header span {
  font-size: 16px;
  font-weight: 600;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.point-tip {
  margin-bottom: 12px;
}
:deep(.el-table) .el-link + .el-divider--vertical {
  margin: 0 4px;
}
</style>