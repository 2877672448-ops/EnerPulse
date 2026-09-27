<template>
  <div class="cloud-scada">
    <el-tabs v-model="activeTab" class="scada-tabs">
      <!-- Tab 1: 组态上传 -->
      <el-tab-pane label="组态上传" name="upload">
        <el-card shadow="never" class="upload-card">
          <template #header>
            <div class="card-header">
              <el-icon><Upload /></el-icon>
              <span>组态文件上传</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="14">
              <el-upload
                ref="uploadRef"
                class="upload-dragger"
                drag
                :action="uploadAction"
                :headers="uploadHeaders"
                :data="uploadData"
                :before-upload="beforeUpload"
                :on-progress="onProgress"
                :on-success="onSuccess"
                :on-error="onError"
                accept=".svg,.png,.jpg,.jpeg"
                :show-file-list="false"
                :auto-upload="false"
                name="file"
              >
                <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                <div class="el-upload__text">
                  将组态文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 SVG / PNG / JPG 格式，单个文件不超过 10MB
                  </div>
                </template>
              </el-upload>
              <el-progress
                v-if="uploadProgress > 0"
                :percentage="uploadProgress"
                :status="progressStatus"
                class="upload-progress"
              />
            </el-col>
            <el-col :span="10">
              <el-form :model="form" label-width="140px" label-position="right">
                <el-form-item label="组态主页文件名" required>
                  <el-input v-model="form.homePageFile" placeholder="请输入组态主页文件名（必填）" />
                </el-form-item>
                <el-form-item label="组态名称">
                  <el-input v-model="form.name" placeholder="请输入组态名称" />
                </el-form-item>
                <el-form-item label="组态描述">
                  <el-input
                    v-model="form.description"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入组态描述"
                  />
                </el-form-item>
                <el-form-item label="所属区域">
                  <el-select v-model="form.area" placeholder="请选择区域" filterable style="width: 100%">
                    <el-option
                      v-for="a in areaOptions"
                      :key="a.value"
                      :label="a.label"
                      :value="a.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="组态类型">
                  <el-radio-group v-model="form.type">
                    <el-radio label="SVG">SVG</el-radio>
                    <el-radio label="PNG">PNG</el-radio>
                    <el-radio label="JPG">JPG</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    :icon="Upload"
                    :loading="uploading"
                    @click="submitUpload"
                  >
                    开始上传
                  </el-button>
                  <el-button :icon="RefreshLeft" @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>

          <el-divider v-if="lastUploaded" />
          <div v-if="lastUploaded" class="upload-success">
            <el-alert
              title="组态上传成功"
              type="success"
              :closable="false"
              show-icon
            >
              <template #default>
                <span>{{ lastUploaded.name }} 已上传，可前往管理页面查看与发布。</span>
              </template>
            </el-alert>
            <div class="success-actions">
              <el-button type="primary" :icon="Right" @click="goManage">
                去管理
              </el-button>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- Tab 2: 组态管理 -->
      <el-tab-pane label="组态管理" name="manage">
        <el-card shadow="never" class="manage-card">
          <template #header>
            <div class="card-header">
              <el-icon><Files /></el-icon>
              <span>组态项目管理</span>
            </div>
          </template>

          <el-form :inline="true" :model="search" class="search-bar">
            <el-form-item label="名称">
              <el-input
                v-model="search.name"
                placeholder="请输入名称"
                clearable
                @keyup.enter="loadList"
              />
            </el-form-item>
            <el-form-item label="区域">
              <el-select
                v-model="search.area"
                placeholder="请选择区域"
                clearable
                filterable
                style="width: 180px"
              >
                <el-option
                  v-for="a in areaOptions"
                  :key="a.value"
                  :label="a.label"
                  :value="a.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="search.status" placeholder="请选择状态" clearable style="width: 140px">
                <el-option label="已发布" value="PUBLISHED" />
                <el-option label="草稿" value="DRAFT" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleScadaSearch">搜索</el-button>
              <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="loading"
            :data="pagedList"
            border
            stripe
            class="scada-table"
          >
            <el-table-column type="index" label="#" width="55" align="center" />
            <el-table-column prop="name" label="组态名称" min-width="140" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="typeTagType(row.type)" size="small">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="area" label="所属区域" width="120" align="center">
              <template #default="{ row }">
                {{ areaLabel(row.area) }}
              </template>
            </el-table-column>
            <el-table-column prop="fileSize" label="文件大小" width="110" align="center">
              <template #default="{ row }">
                {{ formatSize(row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column prop="uploader" label="上传人" width="110" align="center" />
            <el-table-column prop="uploadTime" label="上传时间" width="170" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag
                  :type="row.status === 'PUBLISHED' ? 'success' : 'info'"
                  size="small"
                >
                  {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="460" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" :icon="View" @click="previewRow(row)">预览</el-button>
                <el-button link type="primary" :icon="Refresh" @click="openUpdateDialog(row)">更新</el-button>
                <el-button link type="primary" :icon="Download" @click="downloadScada(row)">下载</el-button>
                <el-button
                  link
                  :type="row.accessType === 'PUBLIC' ? 'success' : 'warning'"
                  @click="toggleAccess(row)"
                >
                  {{ row.accessType === 'PUBLIC' ? '对外开放' : '登陆查看' }}
                </el-button>
                <el-button
                  v-if="row.accessType === 'PUBLIC'"
                  link
                  type="primary"
                  @click="openScadaView(row)"
                >打开组态</el-button>
                <el-button
                  v-if="row.accessType === 'PUBLIC'"
                  link
                  type="info"
                  :icon="CopyDocument"
                  @click="copyAccessUrl(row)"
                >复制链接</el-button>
                <el-button link type="danger" :icon="Delete" @click="deleteRow(row)">删除</el-button>
              </template>
            </el-table-column>

            <template #empty>
              <div class="empty-state">
                <el-icon class="empty-icon"><FolderOpened /></el-icon>
                <p>暂无组态项目</p>
              </div>
            </template>
          </el-table>

          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="page.current"
              v-model:page-size="page.size"
              :page-sizes="[10, 20, 50, 100]"
              :total="filteredList.length"
              layout="total, sizes, prev, pager, next, jumper"
              background
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="组态预览"
      width="80%"
      top="5vh"
      destroy-on-close
    >
      <div class="preview-container" v-loading="previewLoading">
        <div v-if="hasPreview" class="preview-content">
          <img
            v-if="currentRow && currentRow.type !== 'SVG'"
            :src="previewUrl"
            :alt="currentRow ? currentRow.name : ''"
            class="preview-image"
          />
          <div v-else class="preview-svg" v-html="previewSvg"></div>
        </div>
        <div v-else class="preview-empty">
          <el-icon class="empty-icon"><Picture /></el-icon>
          <p>无法预览该组态</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editVisible"
      title="编辑组态信息"
      width="500px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="90px" v-loading="editSaving">
        <el-form-item label="组态名称">
          <el-input v-model="editForm.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="组态描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="editForm.area" placeholder="请选择区域" filterable style="width: 100%">
            <el-option
              v-for="a in areaOptions"
              :key="a.value"
              :label="a.label"
              :value="a.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="组态类型">
          <el-radio-group v-model="editForm.type">
            <el-radio label="SVG">SVG</el-radio>
            <el-radio label="PNG">PNG</el-radio>
            <el-radio label="JPG">JPG</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSaving" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 组态更新对话框 -->
    <el-dialog
      v-model="updateDialogVisible"
      title="组态更新"
      width="500px"
      destroy-on-close
    >
      <el-upload
        ref="updateUploadRef"
        class="update-upload"
        drag
        :auto-upload="false"
        :on-change="onUpdateFileChange"
        :file-list="updateFileList"
        :limit="1"
        accept=".zip"
        :on-exceed="() => ElMessage.warning('只能上传一个文件')"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将组态工程文件拖到此处，或<em>点击选择</em></div>
        <template #tip>
          <div class="el-upload__tip">请选择新的组态工程 ZIP 文件</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="updateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="updateSubmitting" @click="submitUpdate">确认更新</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  Upload,
  UploadFilled,
  RefreshLeft,
  Right,
  Files,
  Search,
  View,
  Delete,
  FolderOpened,
  Picture,
  Refresh,
  Download,
  CopyDocument,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadInstance } from 'element-plus'
import request from '@/utils/request'

type ScadaType = 'SVG' | 'PNG' | 'JPG'
type ScadaStatus = 'PUBLISHED' | 'DRAFT'

interface ScadaItem {
  id: string | number
  name: string
  description?: string
  area: string
  type: ScadaType
  fileSize: number
  uploader: string
  uploadTime: string
  status: ScadaStatus
  fileUrl?: string
  accessType?: 'PUBLIC' | 'LOGIN'
}

interface AreaOption {
  label: string
  value: string
}

const route = useRoute()
const activeTab = ref<'upload' | 'manage'>('upload')

const areaOptions = ref<AreaOption[]>([
  { label: '一号厂房', value: 'A1' },
  { label: '二号厂房', value: 'A2' },
  { label: '配电室', value: 'P1' },
  { label: '锅炉房', value: 'B1' },
  { label: '冷站', value: 'C1' },
])

function areaLabel(value: string): string {
  return areaOptions.value.find((a) => a.value === value)?.label || value || '-'
}

function getToken(): string {
  return localStorage.getItem('token') || ''
}

// ============ Tab1: 上传 ============
const uploadRef = ref<UploadInstance>()
const uploading = ref(false)
const uploadProgress = ref(0)
const progressStatus = ref<'success' | 'exception' | 'warning' | ''>('')
const lastUploaded = ref<ScadaItem | null>(null)

const form = reactive({
  name: '',
  homePageFile: '',
  description: '',
  area: '',
  type: 'SVG' as ScadaType,
})

const uploadAction = '/api/v1/scada/upload'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${getToken()}`,
}))
const uploadData = computed(() => ({
  name: form.name,
  homePageFile: form.homePageFile,
  description: form.description,
  area: form.area,
  type: form.type,
}))

function beforeUpload(file: File): boolean {
  const validTypes = ['image/svg+xml', 'image/png', 'image/jpeg']
  const ext = (file.name.split('.').pop() || '').toUpperCase()
  if (!validTypes.includes(file.type) && !['SVG', 'PNG', 'JPG', 'JPEG'].includes(ext)) {
    ElMessage.error('仅支持 SVG / PNG / JPG 格式')
    return false
  }
  if (file.size / 1024 / 1024 >= 10) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }
  if (!form.name) {
    ElMessage.warning('请先填写组态名称')
    return false
  }
  if (!form.homePageFile) {
    ElMessage.warning('请先填写组态主页文件名')
    return false
  }
  if (!form.area) {
    ElMessage.warning('请先选择所属区域')
    return false
  }
  uploading.value = true
  uploadProgress.value = 0
  progressStatus.value = ''
  return true
}

function onProgress(event: { percent?: number }): void {
  uploadProgress.value = Math.floor(event.percent || 0)
}

function onSuccess(response: any, file: File): void {
  uploading.value = false
  uploadProgress.value = 100
  progressStatus.value = 'success'
  ElMessage.success('组态上传成功')
  const data = response?.data || response || {}
  lastUploaded.value = {
    id: data.id ?? Date.now(),
    name: data.name ?? form.name,
    description: data.description ?? form.description,
    area: data.area ?? form.area,
    type: data.type ?? form.type,
    fileSize: data.fileSize ?? file.size,
    uploader: data.uploader ?? '当前用户',
    uploadTime: data.uploadTime ?? new Date().toLocaleString(),
    status: data.status ?? 'DRAFT',
    fileUrl: data.fileUrl ?? data.url,
  }
}

function onError(): void {
  uploading.value = false
  progressStatus.value = 'exception'
  ElMessage.error('组态上传失败，请稍后重试')
}

function submitUpload(): void {
  uploadRef.value?.submit()
}

function resetForm(): void {
  form.name = ''
  form.homePageFile = ''
  form.description = ''
  form.area = ''
  form.type = 'SVG'
  uploadProgress.value = 0
  progressStatus.value = ''
  lastUploaded.value = null
}

function goManage(): void {
  activeTab.value = 'manage'
  loadList()
}

// ============ Tab2: 管理 ============
const loading = ref(false)
const list = ref<ScadaItem[]>([])
const search = reactive({ name: '', area: '', status: '' })
const page = reactive({ current: 1, size: 10 })

const filteredList = computed<ScadaItem[]>(() => {
  return list.value.filter((item) => {
    if (search.name && !item.name.toLowerCase().includes(search.name.toLowerCase())) return false
    if (search.area && item.area !== search.area) return false
    if (search.status && item.status !== search.status) return false
    return true
  })
})

const pagedList = computed<ScadaItem[]>(() => {
  const start = (page.current - 1) * page.size
  return filteredList.value.slice(start, start + page.size)
})

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await request.get('/scada')
    if (Array.isArray(res)) {
      list.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      list.value = res.data
    } else if (res?.data?.list && Array.isArray(res.data.list)) {
      list.value = res.data.list
    } else {
      list.value = []
    }
  } catch (e) {
    console.error('加载组态列表失败', e)
    list.value = []
  } finally {
    loading.value = false
  }
}

function resetSearch(): void {
  search.name = ''
  search.area = ''
  search.status = ''
  page.current = 1
}

function typeTagType(type: string): 'primary' | 'success' | 'warning' | 'info' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'info'> = {
    SVG: 'primary',
    PNG: 'success',
    JPG: 'warning',
  }
  return map[type] || 'info'
}

function formatSize(size: number): string {
  if (!size) return '-'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / 1024 / 1024).toFixed(2)} MB`
}

// 预览
const previewVisible = ref(false)
const previewLoading = ref(false)
  const previewUrl = ref('')
const previewSvg = ref('')
const currentRow = ref<ScadaItem | null>(null)

const hasPreview = computed(() => !!previewUrl.value || !!previewSvg.value)

async function previewRow(row: ScadaItem): Promise<void> {
  currentRow.value = row
  previewVisible.value = true
  previewLoading.value = true
  previewUrl.value = ''
  previewSvg.value = ''
  try {
    const url = row.fileUrl || `/api/v1/scada/${row.id}/preview`
    if (row.type === 'SVG') {
      const res: any = await request.get(url, { responseType: 'text' } as any)
      previewSvg.value = typeof res === 'string' ? res : res?.data || ''
    } else {
      previewUrl.value = url
    }
  } catch (e) {
    console.error('预览失败', e)
  } finally {
    previewLoading.value = false
  }
}

// 发布
async function publishRow(row: ScadaItem): Promise<void> {
  try {
    await ElMessageBox.confirm(`确定要发布组态「${row.name}」吗？`, '发布确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await request.put(`/scada/${row.id}/publish`)
    ElMessage.success('发布成功')
    row.status = 'PUBLISHED'
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('发布失败')
  }
}

// 删除
async function deleteRow(row: ScadaItem): Promise<void> {
  try {
    await ElMessageBox.confirm(`确定要删除组态「${row.name}」吗？删除后不可恢复。`, '删除确认', {
      type: 'error',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await request.delete(`/scada/${row.id}`)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

// 编辑
const editVisible = ref(false)
const editSaving = ref(false)
const editForm = reactive({
  id: '' as string | number,
  name: '',
  description: '',
  area: '',
  type: 'SVG' as ScadaType,
})

function editRow(row: ScadaItem): void {
  editForm.id = row.id
  editForm.name = row.name
  editForm.description = row.description || ''
  editForm.area = row.area
  editForm.type = row.type
  editVisible.value = true
}

async function saveEdit(): Promise<void> {
  if (!editForm.name.trim()) {
    ElMessage.warning('请输入组态名称')
    return
  }
  editSaving.value = true
  try {
    await request.put(`/scada/${editForm.id}`, {
      name: editForm.name,
      description: editForm.description,
      area: editForm.area,
      type: editForm.type,
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    editSaving.value = false
  }
}

// ============ 组态更新 ============
const updateDialogVisible = ref(false)
const updateFileList = ref<any[]>([])
const updateSubmitting = ref(false)
const currentUpdateRow = ref<ScadaItem | null>(null)
const updateUploadRef = ref<UploadInstance>()
const updateFile = ref<File | null>(null)

function openUpdateDialog(row: ScadaItem): void {
  currentUpdateRow.value = row
  updateFileList.value = []
  updateFile.value = null
  updateDialogVisible.value = true
}

function onUpdateFileChange(file: any): void {
  if (file.raw) {
    updateFile.value = file.raw
  }
}

async function submitUpdate(): Promise<void> {
  if (!currentUpdateRow.value) {
    ElMessage.warning('未选择更新对象')
    return
  }
  if (!updateFile.value) {
    ElMessage.warning('请先选择组态工程文件')
    return
  }
  updateSubmitting.value = true
  try {
    const formData = new FormData()
    formData.append('file', updateFile.value)
    await request.put(`/scada/${currentUpdateRow.value.id}/upload`, formData as any)
    ElMessage.success('组态更新成功')
    updateDialogVisible.value = false
    updateFile.value = null
    updateFileList.value = []
    loadList()
  } catch (e) {
    ElMessage.error('组态更新失败')
  } finally {
    updateSubmitting.value = false
  }
}

// ============ 组态下载 ============
async function downloadScada(row: ScadaItem): Promise<void> {
  try {
    ElMessage.info('开始下载组态工程...')
    const res: any = await request.get(`/scada/${row.id}/download`, {
      responseType: 'blob',
    } as any)
    const blob = res instanceof Blob ? res : new Blob([res], { type: 'application/zip' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${row.name || 'scada'}.zip`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    console.error('下载失败', e)
    ElMessage.error('下载失败')
  }
}

// ============ 访问权限切换 ============
async function toggleAccess(row: ScadaItem): Promise<void> {
  const newType = row.accessType === 'PUBLIC' ? 'LOGIN' : 'PUBLIC'
  const confirmText = newType === 'PUBLIC'
    ? `确定要将组态「${row.name}」的访问权限修改为"对外开放"吗？对外开放后无需登录即可查看。`
    : `确定要将组态「${row.name}」的访问权限修改为"登陆查看"吗？修改后需要登录才能查看。`
  try {
    await ElMessageBox.confirm(confirmText, '权限修改确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await request.put(`/scada/${row.id}/access`, { accessType: newType })
    row.accessType = newType
    ElMessage.success(`访问权限已修改为"${newType === 'PUBLIC' ? '对外开放' : '登陆查看'}"`)  
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('权限修改失败')
  }
}

// ============ 打开组态（对外开放时直接访问）============
function openScadaView(row: ScadaItem): void {
  const url = `${window.location.origin}/scada/view/${row.id}`
  window.open(url, '_blank')
}

// ============ 复制访问链接 ============
async function copyAccessUrl(row: ScadaItem): Promise<void> {
  const accessUrl = `${window.location.origin}/scada/view/${row.id}`
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(accessUrl)
    } else {
      const input = document.createElement('textarea')
      input.value = accessUrl
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
    }
    ElMessage.success('访问链接已复制到剪贴板')
  } catch (e) {
    console.error('复制链接失败', e)
    ElMessage.error('复制链接失败')
  }
}

watch(() => route.query.tab, (val) => {
  if (val === 'manage' || val === 'upload') activeTab.value = val
}, { immediate: true })

function handleScadaSearch() {
  if (!search.name && !search.area && !search.status) {
    ElMessage.warning('查询内容不能为空')
    return
  }
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.cloud-scada {
  padding: 16px;
}

.scada-tabs {
  background: #fff;
  border-radius: 4px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}

.upload-card,
.manage-card {
  margin-top: 8px;
}

.upload-dragger {
  width: 100%;
}

.upload-dragger :deep(.el-upload-dragger) {
  width: 100%;
  padding: 40px 20px;
}

.update-upload {
  width: 100%;
}

.update-upload :deep(.el-upload-dragger) {
  width: 100%;
  padding: 20px;
}

.upload-progress {
  margin-top: 16px;
}

.upload-success {
  margin-top: 12px;
}

.success-actions {
  margin-top: 12px;
  text-align: right;
}

.search-bar {
  margin-bottom: 16px;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.scada-table {
  width: 100%;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.empty-state p {
  margin: 8px 0 0;
  font-size: 14px;
}

.pagination-wrap {
  margin-top: 16px;
  text-align: right;
}

.preview-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  padding: 20px;
}

.preview-content {
  width: 100%;
  text-align: center;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-svg {
  width: 100%;
  max-height: 70vh;
  overflow: auto;
}

.preview-svg :deep(svg) {
  width: 100%;
  height: auto;
}

.preview-empty {
  text-align: center;
  color: #909399;
}

.preview-empty .empty-icon {
  font-size: 48px;
}
</style>