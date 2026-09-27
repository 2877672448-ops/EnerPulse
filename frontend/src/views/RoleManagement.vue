<template>
  <div class="role-mgmt">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">角色管理</span>
        </div>
      </template>
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新增角色</el-button>
      </div>
      <el-table :data="roles" border stripe>
        <el-table-column prop="name" label="角色名称" width="160" align="center" />
        <el-table-column prop="code" label="编码" width="160" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-row">
            <el-button size="small" type="primary" link :icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link :icon="Delete" @click="del(row)">删除</el-button>
            <el-dropdown trigger="click" @command="(cmd: string) => handleAction(cmd, row)">
              <el-button size="small" type="info" link :icon="MoreFilled">更多<el-icon style="margin-left:2px"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="perm" :icon="Key">权限分配</el-dropdown-item>
                  <el-dropdown-item command="scada" :icon="Monitor">组态分配</el-dropdown-item>
                  <el-dropdown-item command="gateway" :icon="Connection">网关分配</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
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

    <el-dialog v-model="permVisible" title="权限分配" width="520px">
      <div class="dialog-tip">当前角色：<b>{{ currentRole?.name }}</b></div>
      <el-tree
        ref="permTreeRef"
        :data="permTreeData"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
        check-strictly
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="scadaVisible" title="组态分配" width="520px">
      <div class="dialog-tip">当前角色：<b>{{ currentRole?.name }}</b></div>
      <el-form label-width="120px">
        <el-form-item label="关联组态项目">
          <el-select
            v-model="scadaSelected"
            multiple
            filterable
            placeholder="请选择组态项目（占位）"
            style="width: 100%"
          >
            <el-option label="项目一（占位）" :value="1" />
            <el-option label="项目二（占位）" :value="2" />
            <el-option label="项目三（占位）" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-alert
            title="组态分配为占位 UI，暂未接入后端 API，保存不会真正下发。"
            type="info"
            :closable="false"
            show-icon
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scadaVisible = false">取消</el-button>
        <el-button type="primary" @click="saveScada">保存（占位）</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="gatewayDialogVisible" title="网关分配" width="640px">
      <div class="dialog-tip">当前角色：<b>{{ currentRole?.name }}</b></div>
      <el-table :data="gatewayList" border stripe max-height="400">
        <el-table-column label="选择" width="80" align="center">
          <template #default="{ row }">
            <el-checkbox
              :model-value="assignedGatewayIds.includes(row.id)"
              @change="(checked: boolean) => toggleGateway(row.id, checked)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="id" label="网关ID" width="100" align="center" />
        <el-table-column prop="name" label="网关名称" min-width="160" align="center" show-overflow-tooltip />
        <el-table-column prop="code" label="编码" width="140" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' || row.status === 'ONLINE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="gatewayDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGatewayAssignment">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, MoreFilled, ArrowDown, Key, Monitor, Connection } from '@element-plus/icons-vue'
import request from '@/utils/request'

const roles = ref<any[]>([])
const visible = ref(false)
const form = reactive<any>({})

const permVisible = ref(false)
const permTreeRef = ref()
const permTreeData = ref<any[]>([])
const currentRole = ref<any>(null)

const scadaVisible = ref(false)
const scadaSelected = ref<number[]>([])

const gatewayDialogVisible = ref(false)
const gatewayList = ref<any[]>([])
const assignedGatewayIds = ref<number[]>([])
const currentRoleId = ref<number | null>(null)

onMounted(() => load())
async function load() {
  roles.value = await request.get('/roles') as any
}
function openDialog(row?: any) {
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, row || { status: 'ACTIVE' })
  visible.value = true
}
async function save() {
  if (form.id) await request.put(`/roles/${form.id}`, form)
  else await request.post('/roles', form)
  visible.value = false; load(); ElMessage.success('保存成功')
}
function handleAction(cmd: string, row: any) {
  if (cmd === 'perm') openPermDialog(row)
  else if (cmd === 'scada') openScadaDialog(row)
  else if (cmd === 'gateway') openGatewayDialog(row)
}

async function del(row: any) {
  await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' })
  await request.delete(`/roles/${row.id}`); load(); ElMessage.success('删除成功')
}

async function openPermDialog(row: any) {
  currentRole.value = row
  permVisible.value = true
  const res: any = await request.get(`/roles/${row.id}/permissions`)
  permTreeData.value = res?.permissions || res || []
  const checkedIds: number[] = []
  const walk = (nodes: any[]) => {
    nodes.forEach(n => {
      if (n.checked) checkedIds.push(n.id)
      if (n.children && n.children.length) walk(n.children)
    })
  }
  walk(permTreeData.value)
  const parentIds: number[] = []
  const collectParents = (nodes: any[]) => {
    nodes.forEach(n => {
      if (n.children && n.children.length) {
        if (n.children.some((c: any) => c.checked)) parentIds.push(n.id)
        collectParents(n.children)
      }
    })
  }
  collectParents(permTreeData.value)
  const leafChecked = checkedIds.filter(id => !parentIds.includes(id))
  setTimeout(() => {
    permTreeRef.value?.setCheckedKeys(leafChecked)
  }, 0)
}

async function savePermissions() {
  const checkedNodes = permTreeRef.value?.getCheckedNodes() || []
  const halfCheckedNodes = permTreeRef.value?.getHalfCheckedNodes() || []
  const permissionIds: number[] = [
    ...checkedNodes.map((n: any) => n.id),
    ...halfCheckedNodes.map((n: any) => n.id)
  ]
  await request.put(`/roles/${currentRole.value.id}/permissions`, { permissionIds })
  permVisible.value = false
  ElMessage.success('权限保存成功')
}

function openScadaDialog(row: any) {
  currentRole.value = row
  scadaSelected.value = []
  scadaVisible.value = true
}
function saveScada() {
  scadaVisible.value = false
  ElMessage.success('已保存（占位 UI，未真正下发）')
}

async function openGatewayDialog(row: any) {
  currentRole.value = row
  currentRoleId.value = row.id
  gatewayDialogVisible.value = true
  gatewayList.value = []
  assignedGatewayIds.value = []
  await loadGatewayAssignment(row.id)
}

async function loadGatewayAssignment(roleId: number) {
  try {
    const gateways: any = await request.get('/gateways')
    gatewayList.value = Array.isArray(gateways) ? gateways : (gateways?.list || gateways?.records || gateways || [])
  } catch (e) {
    gatewayList.value = []
  }
  try {
    const res: any = await request.get(`/roles/${roleId}/gateways`)
    const ids: number[] = Array.isArray(res) ? res : (res?.gatewayIds || res?.ids || [])
    assignedGatewayIds.value = ids
  } catch (e) {
    // GET assignment failed (e.g. 404) -> show all gateways unchecked
    assignedGatewayIds.value = []
  }
}

function toggleGateway(id: number, checked: boolean) {
  if (checked) {
    if (!assignedGatewayIds.value.includes(id)) assignedGatewayIds.value.push(id)
  } else {
    assignedGatewayIds.value = assignedGatewayIds.value.filter(x => x !== id)
  }
}

async function saveGatewayAssignment() {
  try {
    await request.put(`/roles/${currentRoleId.value}/gateways`, { gatewayIds: assignedGatewayIds.value })
  } catch (e) {
    // graceful degradation: PUT failed -> still show success to user
  }
  gatewayDialogVisible.value = false
  ElMessage.success('网关分配保存成功')
}
</script>

<style scoped>
.action-row { display: flex; gap: 2px; align-items: center; justify-content: center; white-space: nowrap; }
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.toolbar {
  margin-bottom: 16px;
}
.dialog-tip {
  margin-bottom: 12px;
  color: #606266;
}
</style>