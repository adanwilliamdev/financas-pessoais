<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <i class="pi pi-wallet"></i>
        </div>
        <h1>Finanças Pessoais</h1>
        <p>Controle suas finanças de forma inteligente</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email">Email</label>
          <div class="input-wrapper">
            <i class="pi pi-envelope input-icon"></i>
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
            <i class="pi pi-lock input-icon"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              id="senha"
              v-model="senha"
              placeholder="Digite sua senha"
              required
            >
            <button type="button" @click="showPassword = !showPassword" class="toggle-password">
              <i :class="showPassword ? 'pi pi-eye-slash' : 'pi pi-eye'"></i>
            </button>
          </div>
        </div>

        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>

        <div v-if="erro" class="error-message">
          <i class="pi pi-exclamation-triangle"></i> {{ erro }}
        </div>

        <div class="login-footer">
          <p>Não tem uma conta? <router-link to="/register">Crie uma agora</router-link></p>
          <div class="credentials">
            <span><i class="pi pi-key"></i> admin@financas.com / admin123</span>
            <span><i class="pi pi-key"></i> usuario@financas.com / usuario123</span>
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
  background: linear-gradient(135deg, #4F46E5 0%, #3B82F6 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 48px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.25);
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
  width: 60px;
  height: 60px;
  background: #EEF2FF;
  color: #4F46E5;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 26px;
}

.login-header h1 {
  font-size: 26px;
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
  font-weight: 600;
  font-size: 13px;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 16px 12px 42px;
  height: 44px;
  border: 1px solid #CBD5E1;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s ease;
  background: #F8FAFC;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #4F46E5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
  background: white;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 15px;
  color: #94A3B8;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 15px;
  color: #94A3B8;
  cursor: pointer;
  padding: 4px;
}

.toggle-password:hover {
  color: #475569;
}

.btn-primary {
  width: 100%;
  padding: 14px;
  background: #4F46E5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
  height: 46px;
}

.btn-primary:hover:not(:disabled) {
  background: #4338CA;
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(79, 70, 229, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background: #FEF2F2;
  color: #EF4444;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  font-weight: 500;
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
  color: #4F46E5;
  text-decoration: none;
  font-weight: 600;
}

.login-footer a:hover {
  text-decoration: underline;
}

.credentials {
  font-size: 12px;
  color: #64748B;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  padding: 12px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.credentials span {
  display: flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }
}
</style>
