<template>
  <div class="login-container">
    <div class="login-showcase">
      <div class="showcase-badge"><i class="pi pi-wallet"></i></div>
      <h2>Finanças Pessoais</h2>
      <p>Acompanhe receitas, despesas e metas em um só lugar, com clareza e no seu ritmo.</p>
      <ul class="showcase-list">
        <li><i class="pi pi-chart-line"></i> Visão completa do seu fluxo de caixa</li>
        <li><i class="pi pi-flag"></i> Metas de economia com progresso em tempo real</li>
        <li><i class="pi pi-shield"></i> Seus dados protegidos com autenticação segura</li>
      </ul>
      <div class="showcase-glow"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <i class="pi pi-wallet"></i>
        </div>
        <h1>Bem-vindo de volta</h1>
        <p>Entre para continuar controlando suas finanças</p>
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
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: #F8FAFC;
}

/* Painel de destaque à esquerda */
.login-showcase {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  padding: 64px;
  background: var(--primary-gradient, linear-gradient(135deg, #6366F1 0%, #4338CA 100%));
  color: white;
}

.showcase-glow {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.16) 0%, transparent 70%);
  top: -120px;
  right: -120px;
  pointer-events: none;
}

.showcase-badge {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.login-showcase h2 {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: -0.02em;
  max-width: 380px;
}

.login-showcase > p {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  max-width: 380px;
  line-height: 1.6;
}

.showcase-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 12px;
}

.showcase-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.95);
}

.showcase-list i {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.14);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  background: white;
  padding: 48px;
  width: 100%;
  max-width: 420px;
  margin: auto;
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

@media (max-width: 900px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .login-showcase {
    display: none;
  }

  .login-card {
    box-shadow: none;
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
