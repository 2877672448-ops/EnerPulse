<template>
  <div class="user-mgmt">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">用户管理</span>
        </div>
      </template>
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名"
          clearable
          style="width:240px"
          @keyup.enter="load"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
      </div>
      <el-table :data="users" border stripe align="center">
        <el-table-column prop="username" label="用户名" width="140" align="center" />
        <el-table-column prop="nickname" label="昵称" width="140" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="180" align="center" />
        <el-table-column prop="phone" label="手机号" width="140" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" link @click="openResetDialog(row)">重置密码</el-button>
            <el-button size="small" type="danger" link @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="密码" v-if="!form.id"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
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

    <el-dialog v-model="resetVisible" title="重置密码" width="440px">
      <el-form :model="resetForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input :model-value="resetForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="resetForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="resetForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReset">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const users = ref<any[]>([])
const keyword = ref('')
const visible = ref(false)
const form = reactive<any>({})

const resetVisible = ref(false)
const resetForm = reactive<{ id?: number; username?: string; newPassword: string; confirmPassword: string }>({
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => load())
async function load() {
  const res: any = await request.get('/users', { params: { keyword: keyword.value } })
  users.value = res?.items || res || []
}
function openDialog(row?: any) {
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, row || { status: 'ACTIVE' })
  visible.value = true
}
async function save() {
  if (form.id) await request.put(`/users/${form.id}`, form)
  else await request.post('/users', form)
  visible.value = false; load(); ElMessage.success('保存成功')
}
async function del(row: any) {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
  await request.delete(`/users/${row.id}`); load(); ElMessage.success('删除成功')
}
function openResetDialog(row: any) {
  resetForm.id = row.id
  resetForm.username = row.username
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
  resetVisible.value = true
}
async function submitReset() {
  if (!resetForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (resetForm.newPassword !== resetForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  await request.put(`/users/${resetForm.id}/reset-password`, { newPassword: resetForm.newPassword })
  resetVisible.value = false
  ElMessage.success('密码重置成功')
}

function handleSearch() {
  if (!keyword.value || keyword.value.trim() === '') {
    ElMessage.warning('查询内容不能为空')
    return
  }
  load()
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
.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}
</style>