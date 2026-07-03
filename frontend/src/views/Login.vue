<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <span>💰</span>
        </div>
        <h1>Finanças Pessoais</h1>
        <p>Controle suas finanças de forma inteligente</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email">Email</label>
          <div class="input-wrapper">
            <span class="input-icon">📧</span>
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              placeholder="seu@email.com"
              required
              autofocus
            >
          </div>
        </div>
        
        <div class="form-group">
          <label for="senha">Senha</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="senha" 
              v-model="senha" 
              placeholder="Digite sua senha"
              required
            >
            <button type="button" @click="showPassword = !showPassword" class="toggle-password">
              {{ showPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>
        </div>
        
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>
        
        <div v-if="erro" class="error-message">
          <span>⚠️</span> {{ erro }}
        </div>
        
        <div class="login-footer">
          <p>Não tem uma conta? <router-link to="/register">Crie uma agora</router-link></p>
          <div class="credentials">
            <span>🔑 admin@financas.com / admin123</span>
            <span>🔑 usuario@financas.com / usuario123</span>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const email = ref('admin@financas.com')
const senha = ref('admin123')
const loading = ref(false)
const erro = ref('')
const showPassword = ref(false)

const handleLogin = async () => {
  loading.value = true
  erro.value = ''
  
  try {
    await authStore.login(email.value, senha.value)
  } catch (error) {
    erro.value = error.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 48px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 32px;
}

.login-header h1 {
  font-size: 28px;
  color: #0F172A;
  margin-bottom: 8px;
  font-weight: 700;
}

.login-header p {
  color: #64748B;
  font-size: 14px;
}

.login-form .form-group {
  margin-bottom: 20px;
}

.login-form label {
  display: block;
  margin-bottom: 6px;
  color: #0F172A;
  font-weight: 500;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 16px 12px 44px;
  height: 44px;
  border: 2px solid #E2E8F0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #F8FAFC;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #2563EB;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
  background: white;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
}

.toggle-password {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  opacity: 0.6;
}

.toggle-password:hover {
  opacity: 1;
}

.btn-primary {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  height: 48px;
}

.btn-primary:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background: #FEE2E2;
  color: #DC2626;
  border-radius: 10px;
  text-align: center;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
}

.login-footer p {
  color: #64748B;
  font-size: 14px;
  margin-bottom: 12px;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.login-footer a:hover {
  text-decoration: underline;
}

.credentials {
  font-size: 12px;
  color: #94A3B8;
  background: #F1F5F9;
  padding: 12px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }
}
</style>