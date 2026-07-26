import { defineStore } from 'pinia'
import api from '../utils/api'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    usuario: JSON.parse(localStorage.getItem('usuario') || 'null')
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    getNome: (state) => state.usuario?.nome || '',
    getEmail: (state) => state.usuario?.email || ''
  },
  
  actions: {
    async login(email, senha) {
      try {
        const response = await api.post('/auth/login', { email, senha })
        
        const { token, nome, email: userEmail } = response.data
        
        this.token = token
        this.usuario = { nome, email: userEmail }
        
        localStorage.setItem('token', token)
        localStorage.setItem('usuario', JSON.stringify({ nome, email: userEmail }))
        
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`
        
        router.push('/dashboard')
        return { success: true }
      } catch (error) {
        console.error('Erro no login:', error.response?.data || error.message)
        const message = error.response?.data?.message || error.message || 'Erro ao fazer login'
        throw new Error(message)
      }
    },
    
    logout() {
      this.token = null
      this.usuario = null
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      delete api.defaults.headers.common['Authorization']
      router.push('/login')
    },
    
    checkAuth() {
      if (this.token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
      }
    }
  }
})