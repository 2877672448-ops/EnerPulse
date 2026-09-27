<template>
  <div class="op-log">
    <el-card>
      <div class="toolbar">
        <el-select v-model="filter.module" placeholder="模块" clearable style="width:140px">
          <el-option label="用户" value="USER" />
          <el-option label="角色" value="ROLE" />
          <el-option label="设备" value="DEVICE" />
          <el-option label="网关" value="GATEWAY" />
          <el-option label="区域" value="AREA" />
          <el-option label="菜单" value="MENU" />
          <el-option label="字典" value="DICTIONARY" />
          <el-option label="费率" value="TARIFF" />
          <el-option label="告警" value="ALARM" />
          <el-option label="能耗" value="ENERGY" />
        </el-select>
        <el-select v-model="filter.action" placeholder="操作" clearable style="width:120px">
          <el-option label="创建" value="CREATE" />
          <el-option label="更新" value="UPDATE" />
          <el-option label="删除" value="DELETE" />
          <el-option label="同步" value="SYNC" />
          <el-option label="导出" value="EXPORT" />
        </el-select>
        <el-date-picker v-model="filter.dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" style="width:260px" />
        <el-button type="primary" @click="handleLogSearch">查询</el-button>
      </div>
      <el-table :data="logs" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="90" />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="userAgent" label="User-Agent" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="load" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const logs = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const filter = reactive<any>({ module: '', action: '', dateRange: null })

onMounted(() => load())
async function load() {
  const params: any = { page: page.value, pageSize: pageSize.value }
  if (filter.module) params.module = filter.module
  if (filter.action) params.action = filter.action
  if (filter.dateRange) { params.startTime = filter.dateRange[0].toISOString(); params.endTime = filter.dateRange[1].toISOString() }
  const res: any = await request.get('/operation-logs', { params })
  logs.value = res.items
  total.value = res.total
}

function handleLogSearch() {
  const hasContent = filter.module || filter.action || (filter.dateRange && filter.dateRange.length > 0)
  if (!hasContent) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  load()
}

</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>