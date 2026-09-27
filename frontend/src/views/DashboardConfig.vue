<template>
  <div class="dashboard-config">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card v-loading="loadingDash">
          <template #header>
            <div class="card-header">
              <span>看板列表</span>
              <el-button type="primary" size="small" :icon="Plus" @click="openDashboardDialog()">新增</el-button>
            </div>
          </template>
          <el-table :data="dashboards" border highlight-current-row @row-click="handleRowClick" size="small">
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="description" label="描述" min-width="120" show-overflow-tooltip />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button size="small" link @click.stop="openDashboardDialog(row)">编辑</el-button>
                <el-button size="small" link type="danger" @click.stop="delDashboard(row)">删除</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无看板" />
            </template>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-loading="loadingWidget">
          <template #header>
            <div class="card-header">
              <span>组件列表<template v-if="currentDashboard"> · {{ currentDashboard.name }}</template></span>
              <el-button type="primary" size="small" :icon="Plus" :disabled="!currentDashboard" @click="openWidgetDialog()">新增组件</el-button>
            </div>
          </template>
          <el-table :data="widgets" border size="small">
            <el-table-column prop="title" label="标题" min-width="140" />
            <el-table-column label="类型" width="100" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ widgetTypeMap[row.type]?.label || row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
            <el-table-column label="可见" width="80" align="center">
              <template #default="{ row }">
                <el-switch v-model="row.visible" @change="toggleVisible(row)" />
              </template>
            </el-table-column>
            <el-table-column prop="config" label="配置" min-width="180" show-overflow-tooltip />
            <el-table-column label="操作" width="140" align="center">
              <template #default="{ row }">
                <el-button size="small" link @click="openWidgetDialog(row)">编辑</el-button>
                <el-button size="small" link type="danger" @click="delWidget(row)">删除</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty v-if="!currentDashboard" description="请先在左侧选择看板" />
              <el-empty v-else description="该看板暂无组件" />
            </template>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dashVisible" :title="dashForm.id ? '编辑看板' : '新增看板'" width="500px">
      <el-form ref="dashFormRef" :model="dashForm" :rules="dashRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dashForm.name" placeholder="请输入看板名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dashForm.description" type="textarea" :rows="3" placeholder="看板描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dashVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDashboard">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="widgetVisible" :title="widgetForm.id ? '编辑组件' : '新增组件'" width="640px">
      <el-form ref="widgetFormRef" :model="widgetForm" :rules="widgetRules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="widgetForm.title" placeholder="组件标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="widgetForm.type" placeholder="选择组件类型" style="width:100%">
            <el-option v-for="opt in widgetTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置JSON" prop="config">
          <el-input v-model="widgetForm.config" type="textarea" :rows="5" placeholder='例如 {"dataSource":"energy","unit":"kWh"}' />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="widgetForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="可见" prop="visible">
          <el-switch v-model="widgetForm.visible" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="widgetVisible = false">取消</el-button>
        <el-button type="primary" @click="saveWidget">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const dashboards = ref<any[]>([])
const widgets = ref<any[]>([])
const currentDashboard = ref<any>(null)
const loadingDash = ref(false)
const loadingWidget = ref(false)

const dashVisible = ref(false)
const dashFormRef = ref<any>()
const dashForm = reactive<any>({})
const dashRules = {
  name: [{ required: true, message: '请输入看板名称', trigger: 'blur' }]
}

const widgetVisible = ref(false)
const widgetFormRef = ref<any>()
const widgetForm = reactive<any>({ visible: true, sortOrder: 0, type: 'LINE', config: '{}' })
const widgetRules = {
  title: [{ required: true, message: '请输入组件标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择组件类型', trigger: 'change' }]
}

const widgetTypeOptions = [
  { value: 'LINE', label: '折线图' },
  { value: 'BAR', label: '柱状图' },
  { value: 'PIE', label: '饼图' },
  { value: 'TABLE', label: '表格' },
  { value: 'STAT', label: '统计卡片' }
]
const widgetTypeMap: Record<string, any> = widgetTypeOptions.reduce((m, o) => {
  m[o.value] = o
  return m
}, {} as Record<string, any>)

onMounted(() => loadDashboards())

async function loadDashboards() {
  loadingDash.value = true
  try {
    dashboards.value = (await request.get('/dashboards')) as any
    if (dashboards.value.length && !currentDashboard.value) {
      handleRowClick(dashboards.value[0])
    }
  } finally {
    loadingDash.value = false
  }
}

function handleRowClick(row: any) {
  if (!row) return
  currentDashboard.value = row
  loadWidgets()
}

async function loadWidgets() {
  if (!currentDashboard.value) {
    widgets.value = []
    return
  }
  loadingWidget.value = true
  try {
    widgets.value = (await request.get(`/dashboards/${currentDashboard.value.id}/widgets`)) as any
  } finally {
    loadingWidget.value = false
  }
}

function openDashboardDialog(row?: any) {
  Object.keys(dashForm).forEach(k => delete dashForm[k])
  Object.assign(dashForm, row || { name: '', description: '' })
  dashVisible.value = true
  dashFormRef.value?.clearValidate()
}

async function saveDashboard() {
  await dashFormRef.value?.validate()
  if (dashForm.id) await request.put(`/dashboards/${dashForm.id}`, dashForm)
  else await request.post('/dashboards', dashForm)
  dashVisible.value = false
  const prevId = currentDashboard.value?.id
  await loadDashboards()
  if (prevId) {
    const upd = dashboards.value.find(d => d.id === prevId)
    if (upd) currentDashboard.value = upd
  }
  ElMessage.success('保存成功')
}

async function delDashboard(row: any) {
  await ElMessageBox.confirm('确认删除该看板？删除后其下组件也将被删除。', '提示', { type: 'warning' })
  await request.delete(`/dashboards/${row.id}`)
  if (currentDashboard.value?.id === row.id) {
    currentDashboard.value = null
    widgets.value = []
  }
  loadDashboards()
  ElMessage.success('删除成功')
}

function openWidgetDialog(row?: any) {
  Object.keys(widgetForm).forEach(k => delete widgetForm[k])
  Object.assign(widgetForm, row || { visible: true, sortOrder: 0, type: 'LINE', config: '{}' })
  widgetVisible.value = true
  widgetFormRef.value?.clearValidate()
}

async function saveWidget() {
  await widgetFormRef.value?.validate()
  try {
    JSON.parse(widgetForm.config || '{}')
  } catch {
    ElMessage.error('配置JSON格式错误')
    return
  }
  const dashId = currentDashboard.value.id
  if (widgetForm.id) await request.put(`/dashboards/${dashId}/widgets/${widgetForm.id}`, widgetForm)
  else await request.post(`/dashboards/${dashId}/widgets`, widgetForm)
  widgetVisible.value = false
  loadWidgets()
  ElMessage.success('保存成功')
}

async function delWidget(row: any) {
  await ElMessageBox.confirm('确认删除该组件？', '提示', { type: 'warning' })
  await request.delete(`/dashboards/${currentDashboard.value.id}/widgets/${row.id}`)
  loadWidgets()
  ElMessage.success('删除成功')
}

async function toggleVisible(row: any) {
  const prev = !row.visible
  try {
    await request.put(`/dashboards/${currentDashboard.value.id}/widgets/${row.id}`, row)
    ElMessage.success('已更新可见性')
  } catch {
    row.visible = prev
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>