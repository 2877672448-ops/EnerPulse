<template>
  <div class="tariff-mgmt">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新增费率</el-button>
      </div>
      <el-table :data="tariffs" border stripe>
        <el-table-column prop="name" label="费率名称" width="160" />
        <el-table-column prop="energyTypeId" label="能源类型ID" width="110" />
        <el-table-column prop="periodType" label="时段类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.periodType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价(元)" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="110" />
        <el-table-column prop="endTime" label="结束时间" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑费率' : '新增费率'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="能源类型ID"><el-input-number v-model="form.energyTypeId" :min="1" /></el-form-item>
        <el-form-item label="时段类型">
          <el-select v-model="form.periodType">
            <el-option label="尖峰" value="PEAK" />
            <el-option label="高峰" value="HIGH" />
            <el-option label="平段" value="FLAT" />
            <el-option label="低谷" value="VALLEY" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="4" :step="0.01" /></el-form-item>
        <el-form-item label="开始时间"><el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" /></el-form-item>
        <el-form-item label="结束时间"><el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tariffs = ref<any[]>([])
const visible = ref(false)
const form = reactive<any>({})

onMounted(() => load())
async function load() {
  tariffs.value = await request.get('/tariffs') as any
}
function openDialog(row?: any) {
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, row || { periodType: 'FLAT', status: 'ACTIVE', price: 0 })
  visible.value = true
}
async function save() {
  if (form.id) await request.put(`/tariffs/${form.id}`, form)
  else await request.post('/tariffs', form)
  visible.value = false; load(); ElMessage.success('保存成功')
}
async function del(row: any) {
  await ElMessageBox.confirm('确认删除该费率？', '提示', { type: 'warning' })
  await request.delete(`/tariffs/${row.id}`); load(); ElMessage.success('删除成功')
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>