<template>
  <div class="peak-valley-flat">
    <el-card class="timeline-card">
      <template #header>
        <div class="card-header">
          <span>24小时时段分布</span>
          <el-button type="primary" size="small" :icon="Plus" @click="openDialog()">新增时段</el-button>
        </div>
      </template>
      <el-alert
        v-if="coverageWarn"
        :title="coverageWarn"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom:12px"
      />
      <div class="timeline">
        <div class="timeline-track">
          <div
            v-for="seg in segments"
            :key="seg.id"
            class="timeline-seg"
            :style="{ left: seg.left + '%', width: seg.width + '%', background: seg.color }"
            :title="seg.tooltip"
          >
            <span v-if="seg.width > 8" class="seg-label">{{ seg.name }}</span>
          </div>
          <div v-if="!segments.length" class="timeline-empty">暂无时段数据</div>
        </div>
        <div class="timeline-axis">
          <span
            v-for="h in [0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24]"
            :key="h"
            class="axis-tick"
            :style="{ left: (h / 24) * 100 + '%' }"
          >{{ h.toString().padStart(2, '0') }}</span>
        </div>
      </div>
      <div class="legend">
        <span v-for="t in periodTypes" :key="t.value" class="legend-item">
          <i class="legend-dot" :style="{ background: t.color }"></i>{{ t.label }}
        </span>
      </div>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="tariffs" border stripe v-loading="loading">
        <el-table-column prop="name" label="时段名称" min-width="140" />
        <el-table-column label="时段类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :color="periodColor(row.periodType)" effect="dark" size="small">
              {{ periodLabel(row.periodType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="能源类型" width="100" align="center">
          <template #default="{ row }">{{ energyLabel(row.energyTypeId) }}</template>
        </el-table-column>
        <el-table-column label="单价(元)" width="120" align="right">
          <template #default="{ row }">¥{{ Number(row.price || 0).toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="100" align="center" />
        <el-table-column prop="endTime" label="结束时间" width="100" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑时段' : '新增时段'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="时段名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：尖峰时段一" />
        </el-form-item>
        <el-form-item label="时段类型" prop="periodType">
          <el-select v-model="form.periodType" placeholder="选择时段类型" style="width:100%">
            <el-option v-for="t in periodTypes" :key="t.value" :label="t.label" :value="t.value">
              <i class="period-dot" :style="{ background: t.color }"></i>
              <span>{{ t.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="能源类型" prop="energyTypeId">
          <el-select v-model="form.energyTypeId" placeholder="选择能源类型" style="width:100%">
            <el-option v-for="e in energyTypes" :key="e.value" :label="e.label" :value="e.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价(元)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="4" :step="0.01" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" placeholder="HH:mm" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" placeholder="HH:mm" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const tariffs = ref<any[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<any>()
const form = reactive<any>({ periodType: 'FLAT', status: 'ACTIVE', price: 0, energyTypeId: 1 })
const rules = {
  name: [{ required: true, message: '请输入时段名称', trigger: 'blur' }],
  periodType: [{ required: true, message: '请选择时段类型', trigger: 'change' }],
  energyTypeId: [{ required: true, message: '请选择能源类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const periodTypes = [
  { value: 'PEAK', label: '尖峰', color: '#F56C6C' },
  { value: 'HIGH', label: '高峰', color: '#E6A23C' },
  { value: 'FLAT', label: '平段', color: '#409EFF' },
  { value: 'VALLEY', label: '低谷', color: '#67C23A' }
]
const periodColorMap = periodTypes.reduce((m, t) => { m[t.value] = t.color; return m }, {} as Record<string, string>)
const periodLabelMap = periodTypes.reduce((m, t) => { m[t.value] = t.label; return m }, {} as Record<string, string>)

const energyTypes = [
  { value: 1, label: '电力' },
  { value: 2, label: '水' },
  { value: 3, label: '燃气' },
  { value: 4, label: '蒸汽' }
]

function periodColor(t: string) { return periodColorMap[t] || '#909399' }
function periodLabel(t: string) { return periodLabelMap[t] || t }
function energyLabel(id: number) {
  const e = energyTypes.find(x => x.value === id)
  return e ? e.label : ('类型' + id)
}

function toMinutes(t: string): number {
  if (!t) return -1
  const parts = t.split(':')
  const h = parseInt(parts[0], 10)
  const m = parseInt(parts[1] || '0', 10)
  if (h === 24) return 1440
  return h * 60 + m
}

function fmt(min: number): string {
  const m = ((min % 1440) + 1440) % 1440
  const h = Math.floor(m / 60)
  const mm = m % 60
  return `${h.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}`
}

const segments = computed(() => {
  const segs: any[] = []
  for (const t of tariffs.value.filter(x => x.status === 'ACTIVE')) {
    let s = toMinutes(t.startTime)
    let e = toMinutes(t.endTime)
    if (e <= s) e += 1440
    const color = periodColor(t.periodType)
    const tooltip = `${t.name} ${t.startTime}-${t.endTime} ¥${t.price}`
    if (e <= 1440) {
      segs.push({
        id: t.id,
        name: t.name,
        tooltip,
        color,
        left: (s / 1440) * 100,
        width: ((e - s) / 1440) * 100
      })
    } else {
      segs.push({
        id: t.id + '_a',
        name: t.name,
        tooltip,
        color,
        left: (s / 1440) * 100,
        width: ((1440 - s) / 1440) * 100
      })
      segs.push({
        id: t.id + '_b',
        name: t.name,
        tooltip,
        color,
        left: 0,
        width: ((e - 1440) / 1440) * 100
      })
    }
  }
  return segs
})

const coverageWarn = computed(() => {
  const active = tariffs.value.filter(t => t.status === 'ACTIVE')
  if (!active.length) return ''
  const intervals = active.map(t => {
    let s = toMinutes(t.startTime)
    let e = toMinutes(t.endTime)
    if (e <= s) e += 1440
    return { s, e, name: t.name }
  }).sort((a, b) => a.s - b.s)
  const gaps: string[] = []
  const overlaps: string[] = []
  let cursor = 0
  for (const i of intervals) {
    const start = i.s
    const end = Math.min(i.e, 1440)
    if (start > cursor) gaps.push(`${fmt(cursor)}~${fmt(start)}`)
    else if (start < cursor) overlaps.push(`${i.name}(${fmt(start)}~${fmt(end)})`)
    cursor = Math.max(cursor, end)
  }
  if (cursor < 1440) gaps.push(`${fmt(cursor)}~24:00`)
  const msgs: string[] = []
  if (gaps.length) msgs.push('时段存在空缺：' + gaps.join('、'))
  if (overlaps.length) msgs.push('时段存在重叠：' + overlaps.join('、'))
  return msgs.join('；')
})

onMounted(() => load())

async function load() {
  loading.value = true
  try {
    tariffs.value = (await request.get('/tariffs')) as any
  } finally {
    loading.value = false
  }
}

function openDialog(row?: any) {
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, row || { periodType: 'FLAT', status: 'ACTIVE', price: 0, energyTypeId: 1 })
  visible.value = true
  formRef.value?.clearValidate()
}

async function save() {
  await formRef.value?.validate()
  if (form.id) await request.put(`/tariffs/${form.id}`, form)
  else await request.post('/tariffs', form)
  visible.value = false
  load()
  ElMessage.success('保存成功')
}

async function del(row: any) {
  await ElMessageBox.confirm('确认删除该时段？', '提示', { type: 'warning' })
  await request.delete(`/tariffs/${row.id}`)
  load()
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.timeline { margin-top: 8px; }
.timeline-track { position: relative; height: 40px; background: #f5f7fa; border-radius: 4px; overflow: hidden; }
.timeline-seg { position: absolute; top: 0; bottom: 0; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 12px; overflow: hidden; cursor: default; }
.timeline-empty { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; color: #909399; font-size: 13px; }
.timeline-axis { position: relative; height: 24px; margin-top: 4px; }
.axis-tick { position: absolute; top: 0; transform: translateX(-50%); font-size: 11px; color: #909399; }
.legend { margin-top: 12px; display: flex; gap: 16px; }
.legend-item { display: flex; align-items: center; font-size: 12px; color: #606266; }
.legend-dot { display: inline-block; width: 12px; height: 12px; border-radius: 2px; margin-right: 4px; }
.period-dot { display: inline-block; width: 10px; height: 10px; border-radius: 2px; margin-right: 8px; }
</style>