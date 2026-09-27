<template>
  <div class="menu-mgmt">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增菜单</el-button>
      </div>
      <el-table :data="menus" border stripe v-loading="loading">
        <el-table-column prop="name" label="菜单名称" min-width="140" />
        <el-table-column prop="path" label="路由路径" min-width="140" />
        <el-table-column label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon && iconMap[row.icon]" :size="18">
              <component :is="iconMap[row.icon]" />
            </el-icon>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortNo" label="排序" width="80" align="center" />
        <el-table-column prop="permissionCode" label="权限标识" min-width="120" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="公开访问" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.publicAccess ? 'warning' : 'info'" size="small">
              {{ row.publicAccess ? '已开启' : '未开启' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="openPublicDialog(row)">公开链接</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑菜单' : '新增菜单'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="例如 /system/users" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="form.icon" placeholder="选择图标" filterable clearable style="width:100%">
            <el-option v-for="name in iconNames" :key="name" :label="name" :value="name">
              <el-icon style="vertical-align:middle"><component :is="iconMap[name]" /></el-icon>
              <span style="margin-left:8px">{{ name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortNo">
          <el-input-number v-model="form.sortNo" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="例如 system:user:list" />
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

    <el-dialog v-model="publicVisible" title="公开访问链接" width="560px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="菜单名称">{{ publicInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="公开状态">
          <el-tag :type="publicInfo.publicAccess ? 'success' : 'info'" size="small">
            {{ publicInfo.publicAccess ? '已开启' : '已关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="publicInfo.publicAccess && publicInfo.url" label="访问链接">
          <el-input v-model="publicInfo.url" readonly>
            <template #append>
              <el-button :icon="CopyDocument" @click="copyLink">复制</el-button>
            </template>
          </el-input>
        </el-descriptions-item>
        <el-descriptions-item v-if="!publicInfo.publicAccess" label="提示">
          开启后，任何人可通过公开链接访问此菜单（无需登录）。
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="publicVisible = false">关闭</el-button>
        <el-button
          :type="publicInfo.publicAccess ? 'warning' : 'primary'"
          :loading="toggling"
          @click="togglePublic()"
        >
          {{ publicInfo.publicAccess ? '关闭公开访问' : '开启公开访问' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, CopyDocument, Menu, Setting, User, Document, DataLine,
  TrendCharts, Monitor, Cpu, Warning, House, Bell, Files, PieChart, Histogram
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const menus = ref<any[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<any>()
const form = reactive<any>({})
const rules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const iconMap: Record<string, any> = {
  Menu, Setting, User, Document, DataLine, TrendCharts,
  Monitor, Cpu, Warning, House, Bell, Files, PieChart, Histogram
}
const iconNames = Object.keys(iconMap)

const publicVisible = ref(false)
const publicInfo = reactive<any>({ id: null, name: '', publicAccess: false, publicToken: '', url: '' })
const toggling = ref(false)

onMounted(() => load())

async function load() {
  loading.value = true
  try {
    menus.value = (await request.get('/menus')) as any
  } finally {
    loading.value = false
  }
}

function openDialog(row?: any) {
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, row || { status: 'ACTIVE', sortNo: 0, icon: 'Menu' })
  visible.value = true
  formRef.value?.clearValidate()
}

async function save() {
  await formRef.value?.validate()
  if (form.id) await request.put(`/menus/${form.id}`, form)
  else await request.post('/menus', form)
  visible.value = false
  load()
  ElMessage.success('保存成功')
}

async function del(row: any) {
  await ElMessageBox.confirm('确认删除该菜单？', '提示', { type: 'warning' })
  await request.delete(`/menus/${row.id}`)
  load()
  ElMessage.success('删除成功')
}

function openPublicDialog(row: any) {
  Object.assign(publicInfo, {
    id: row.id,
    name: row.name,
    publicAccess: !!row.publicAccess,
    publicToken: row.publicToken || '',
    url: ''
  })
  if (row.publicAccess && row.publicToken) {
    publicInfo.url = buildPublicUrl(row.publicToken)
  }
  publicVisible.value = true
}

function buildPublicUrl(token: string) {
  return `${window.location.origin}/public/menu/${token}`
}

async function togglePublic() {
  toggling.value = true
  try {
    const res: any = await request.post(`/menus/${publicInfo.id}/public-link`)
    await load()
    const updated = menus.value.find((m: any) => m.id === publicInfo.id)
    if (updated) {
      publicInfo.publicAccess = !!updated.publicAccess
      publicInfo.publicToken = updated.publicToken || ''
    } else if (res?.publicToken) {
      publicInfo.publicAccess = true
      publicInfo.publicToken = res.publicToken
    } else {
      publicInfo.publicAccess = !publicInfo.publicAccess
    }
    publicInfo.url = (publicInfo.publicAccess && publicInfo.publicToken) ? buildPublicUrl(publicInfo.publicToken) : ''
    ElMessage.success(publicInfo.publicAccess ? '已开启公开访问' : '已关闭公开访问')
  } finally {
    toggling.value = false
  }
}

async function copyLink() {
  if (!publicInfo.url) return
  try {
    await navigator.clipboard.writeText(publicInfo.url)
    ElMessage.success('链接已复制')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>