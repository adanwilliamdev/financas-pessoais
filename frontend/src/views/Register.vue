<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="logo">
          <span>💰</span>
        </div>
        <h1>Criar Conta</h1>
        <p>Cadastre-se para começar a controlar suas finanças</p>
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="nome">Nome</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              id="nome" 
              v-model="nome" 
              placeholder="Seu nome completo"
              required
            >
          </div>
        </div>
        
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
              placeholder="Mínimo 6 caracteres"
              required
              minlength="6"
            >
            <button type="button" @click="showPassword = !showPassword" class="toggle-password">
              {{ showPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>
          <small class="text-muted">A senha deve ter no mínimo 6 caracteres</small>
        </div>
        
        <div class="form-group">
          <label for="confirmarSenha">Confirmar Senha</label>
          <div class="input-wrapper">
            <span class="input-icon">✅</span>
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              id="confirmarSenha" 
              v-model="confirmarSenha" 
              placeholder="Confirme sua senha"
              required
            >
            <button type="button" @click="showConfirmPassword = !showConfirmPassword" class="toggle-password">
              {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>
        </div>
        
        <button type="submit" class="btn btn-primary" :disabled="carregando">
          {{ carregando ? 'Cadastrando...' : 'Cadastrar' }}
        </button>
        
        <div v-if="erro" class="error-message">
          <span>⚠️</span> {{ erro }}
        </div>
        
        <div v-if="sucesso" class="success-message">
          <span>✅</span> {{ sucesso }}
        </div>
        
        <div class="register-footer">
          <p>Já tem uma conta? <router-link to="/login">Faça login</router-link></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/api'

const router = useRouter()
const nome = ref('')
const email = ref('')
const senha = ref('')
const confirmarSenha = ref('')
const carregando = ref(false)
const erro = ref('')
const sucesso = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const handleRegister = async () => {
  if (senha.value !== confirmarSenha.value) {
    erro.value = 'As senhas não coincidem'
    return
  }
  
  if (senha.value.length < 6) {
    erro.value = 'A senha deve ter no mínimo 6 caracteres'
    return
  }
  
  carregando.value = true
  erro.value = ''
  sucesso.value = ''
  
  try {
    await api.post('/auth/register', {
      nome: nome.value.trim(),
      email: email.value.trim(),
      senha: senha.value
    })
    
    sucesso.value = '✅ Cadastro realizado com sucesso! Redirecionando...'
    
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    
  } catch (error) {
    erro.value = error.response?.data?.erro || 'Erro ao cadastrar. Tente novamente.'
  } finally {
    carregando.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
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

.register-header {
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

.register-header h1 {
  font-size: 28px;
  color: #0F172A;
  margin-bottom: 8px;
  font-weight: 700;
}

.register-header p {
  color: #64748B;
  font-size: 14px;
}

.register-form .form-group {
  margin-bottom: 20px;
}

.register-form label {
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
  box-sizing: border-box;
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

.text-muted {
  font-size: 12px;
  color: #94A3B8;
  margin-top: 4px;
  display: block;
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

.success-message {
  margin-top: 16px;
  padding: 12px;
  background: #DCFCE7;
  color: #16A34A;
  border-radius: 10px;
  text-align: center;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-footer {
  margin-top: 24px;
  text-align: center;
}

.register-footer p {
  color: #64748B;
  font-size: 14px;
}

.register-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.register-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-card {
    padding: 32px 24px;
  }
}
</style>