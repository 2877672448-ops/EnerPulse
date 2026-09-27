<template>
  <div class="login-container">
    <div class="login-bg-orb login-bg-orb-1"></div>
    <div class="login-bg-orb login-bg-orb-2"></div>
    <div class="login-box">
      <div class="login-header">
        <div class="login-logo">
          <el-icon :size="32" color="#fff"><Lightning /></el-icon>
        </div>
        <h1>EnerPulse</h1>
        <p>能耗管理平台</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%; margin-top: 4px" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="login-tip">默认账号 admin / 123456</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Lightning } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.doLogin(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #14172B;
  position: relative;
  overflow: hidden;
}
.login-bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
}
.login-bg-orb-1 {
  width: 500px; height: 500px;
  background: #4F7CFF;
  top: -150px; left: -100px;
}
.login-bg-orb-2 {
  width: 400px; height: 400px;
  background: #36D1DC;
  bottom: -100px; right: -80px;
}
.login-box {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 48px 40px 40px;
  background: rgba(255,255,255,.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 24px 64px rgba(0,0,0,.25);
  border: 1px solid rgba(255,255,255,.15);
}
.login-header { text-align: center; margin-bottom: 32px; }
.login-logo {
  width: 64px; height: 64px;
  margin: 0 auto 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, #4F7CFF 0%, #36D1DC 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(79,124,255,.4);
}
.login-header h1 { font-size: 28px; color: #14172B; margin-bottom: 4px; font-weight: 700; }
.login-header p { color: #6B7280; font-size: 14px; }
.login-tip { text-align: center; color: #9CA3AF; font-size: 12px; margin-top: 20px; }
</style>