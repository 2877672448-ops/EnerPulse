<template>
  <div class="dict-mgmt">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">字典管理</span>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="6">
          <div class="type-panel">
            <div class="type-panel-title">字典类型</div>
            <div
              v-for="t in presetTypes"
              :key="t.id"
              class="type-item"
              :class="{ active: currentType?.id === t.id }"
              @click="selectType(t)"
            >
              <el-icon class="type-icon"><component :is="t.icon" /></el-icon>
              <div class="type-text">
                <div class="type-name">{{ t.name }}</div>
                <div class="type-code">{{ t.code }}</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="toolbar">
            <span class="current-label" v-if="currentType">
              当前类型：<b>{{ currentType.name }}</b>（{{ currentType.code }}）
            </span>
            <el-button type="primary" :disabled="!currentType" @click="openItemDialog()">新增字典项</el-button>
          </div>
          <el-table :data="items" border stripe>
            <el-table-column prop="code" label="编码" width="160" align="center" />
            <el-table-column prop="name" label="名称" min-width="160" align="center" />
            <el-table-column prop="unit" label="单位" width="100" align="center" />
            <el-table-column prop="sortNo" label="排序" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="openItemDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" link @click="delItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="itemVisible" title="字典项" width="440px">
      <el-form :model="itemForm" label-width="80px">
        <el-form-item label="编码"><el-input v-model="itemForm.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="itemForm.name" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="itemForm.unit" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="itemForm.sortNo" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="itemForm.status">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, markRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lightning, DataLine, Link, Setting, Coin, Odometer } from '@element-plus/icons-vue'
import request from '@/utils/request'

const presetTypes = ref<any[]>([
  { id: 1, name: '能耗类型', code: 'EnergyType', icon: markRaw(Lightning) },
  { id: 2, name: '能耗分项', code: 'EnergyItem', icon: markRaw(DataLine) },
  { id: 3, name: '首页链接', code: 'HomePage', icon: markRaw(Link) },
  { id: 4, name: '系统名称', code: 'SystemName', icon: markRaw(Setting) },
  { id: 5, name: '费率单位', code: 'TariffUnit', icon: markRaw(Coin) },
  { id: 6, name: '数值单位', code: 'DataUnit', icon: markRaw(Odometer) }
])

const items = ref<any[]>([])
const currentType = ref<any>(null)
const itemVisible = ref(false)
const itemForm = reactive<any>({})

onMounted(() => {
  loadTypes()
})

async function loadTypes() {
  try {
    await request.get('/dictionaries/types')
  } catch (e) {
    // 预设类型不依赖后端返回，仍展示 6 个
  }
}

async function selectType(row: any) {
  currentType.value = row
  try {
    const res: any = await request.get(`/dictionaries/items`, { params: { typeId: row.id } })
    items.value = res?.items || res || []
  } catch (e) {
    items.value = []
  }
}

function openItemDialog(row?: any) {
  Object.keys(itemForm).forEach(k => delete itemForm[k])
  Object.assign(itemForm, row || { typeId: currentType.value?.id, status: 'ACTIVE', sortNo: 0 })
  itemVisible.value = true
}
async function saveItem() {
  if (itemForm.id) await request.put(`/dictionaries/items/${itemForm.id}`, itemForm)
  else await request.post(`/dictionaries/items`, { ...itemForm, typeId: currentType.value.id })
  itemVisible.value = false; selectType(currentType.value); ElMessage.success('保存成功')
}
async function delItem(row: any) {
  await ElMessageBox.confirm('确认删除该字典项？', '提示', { type: 'warning' })
  await request.delete(`/dictionaries/items/${row.id}`); selectType(currentType.value); ElMessage.success('删除成功')
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.type-panel {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}
.type-panel-title {
  padding: 12px 16px;
  background: #f5f7fa;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
}
.type-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}
.type-item:last-child {
  border-bottom: none;
}
.type-item:hover {
  background-color: #f0f7ff;
}
.type-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409eff;
  padding-left: 13px;
}
.type-icon {
  font-size: 20px;
  color: #409eff;
  margin-right: 12px;
}
.type-text {
  display: flex;
  flex-direction: column;
}
.type-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.type-code {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.current-label {
  color: #606266;
  font-size: 14px;
}
</style>