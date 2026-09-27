import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getCurrentUser } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<any>(null)

  async function doLogin(username: string, password: string) {
    const res: any = await login({ username, password })
    token.value = res.accessToken
    localStorage.setItem('token', res.accessToken)
    await fetchUser()
  }

  async function fetchUser() {
    try {
      user.value = await getCurrentUser()
    } catch {
      user.value = null
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, doLogin, fetchUser, logout }
})
