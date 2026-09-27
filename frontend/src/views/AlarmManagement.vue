<template>
  <div class="alarm-mgmt">
    <el-card>
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="statusFilter" clearable style="width:150px">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="已确认" value="ACKED" />
            <el-option label="已恢复" value="RECOVERED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAlarmSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top:20px">
      <el-table :data="alarms" border stripe>
        <el-table-column prop="alarmCode" label="告警编码" width="140" />
        <el-table-column prop="alarmType" label="类型" width="100" />
        <el-table-column prop="level" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="levelType(row.level)">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="告警内容" />
        <el-table-column prop="value" label="当前值" width="100" />
        <el-table-column prop="threshold" label="阈值" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="occurredAt" label="发生时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.status === 'ACTIVE'" size="small" type="warning" @click="ack(row)">确认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const alarms = ref<any[]>([])
const statusFilter = ref('')

onMounted(() => loadAlarms())
async function loadAlarms() {
  const params: any = {}
  if (statusFilter.value) params.status = statusFilter.value
  alarms.value = await request.get('/alarms', { params }) as any
}
async function ack(row: any) {
  await request.put(`/alarms/${row.id}/ack`)
  ElMessage.success('已确认')
  loadAlarms()
}
function levelType(level: string) {
  return { CRITICAL: 'danger', MAJOR: 'warning', MINOR: 'info' }[level] || ''
}
function statusType(status: string) {
  return { ACTIVE: 'danger', ACKED: 'warning', RECOVERED: 'success' }[status] || ''
}

function handleAlarmSearch() {
  if (!statusFilter.value || statusFilter.value === '') {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadAlarms()
}

</script>
