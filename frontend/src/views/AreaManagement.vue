<template>
  <div class="area-mgmt">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新增区域</el-button>
      </div>
      <el-table :data="areas" border stripe row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="name" label="区域名称" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="areaType" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
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

    <el-dialog v-model="visible" title="区域" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.areaType">
            <el-option label="厂区" value="FACTORY" />
            <el-option label="车间" value="WORKSHOP" />
            <el-option label="楼层" value="FLOOR" />
            <el-option label="房间" value="ROOM" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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

const areas = ref<any[]>([])
const visible = ref(false)
const form = reactive<any>({})

onMounted(() => load())
async function load() {
  areas.value = await request.get('/areas/tree') as any
}
function openDialog(row?: any) {
  Object.assign(form, row || { areaType: 'FACTORY', status: 'ACTIVE', sort: 0 })
  visible.value = true
}
async function save() {
  if (form.id) await request.put(`/areas/${form.id}`, form)
  else await request.post('/areas', form)
  visible.value = false; load(); ElMessage.success('保存成功')
}
async function del(row: any) {
  await ElMessageBox.confirm('确认删除该区域？', '提示', { type: 'warning' })
  await request.delete(`/areas/${row.id}`); load(); ElMessage.success('删除成功')
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
