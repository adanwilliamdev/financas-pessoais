<template>
  <div class="register-container">
    <div class="register-showcase">
      <div class="showcase-badge"><i class="pi pi-wallet"></i></div>
      <h2>Comece a organizar suas finanças hoje</h2>
      <p>Crie sua conta gratuita e tenha controle total sobre receitas, despesas e metas.</p>
      <ul class="showcase-list">
        <li><i class="pi pi-check"></i> Cadastro rápido, sem cartão de crédito</li>
        <li><i class="pi pi-bolt"></i> Dashboard pronto em segundos</li>
        <li><i class="pi pi-lock"></i> Seus dados sempre protegidos</li>
      </ul>
      <div class="showcase-glow"></div>
    </div>

    <div class="register-card">
      <div class="register-header">
        <div class="logo">
          <i class="pi pi-wallet"></i>
        </div>
        <h1>Criar Conta</h1>
        <p>Cadastre-se para começar a controlar suas finanças</p>
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="nome">Nome</label>
          <div class="input-wrapper">
            <i class="pi pi-user input-icon"></i>
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
            <i class="pi pi-envelope input-icon"></i>
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
            <i class="pi pi-lock input-icon"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="senha" 
              v-model="senha" 
              placeholder="Mínimo 6 caracteres"
              required
              minlength="6"
            >
            <button type="button" @click="showPassword = !showPassword" class="toggle-password">
              <i :class="showPassword ? 'pi pi-eye-slash' : 'pi pi-eye'"></i>
            </button>
          </div>
          <small class="text-muted">A senha deve ter no mínimo 6 caracteres</small>
        </div>
        
        <div class="form-group">
          <label for="confirmarSenha">Confirmar Senha</label>
          <div class="input-wrapper">
            <i class="pi pi-check-circle input-icon"></i>
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              id="confirmarSenha" 
              v-model="confirmarSenha" 
              placeholder="Confirme sua senha"
              required
            >
            <button type="button" @click="showConfirmPassword = !showConfirmPassword" class="toggle-password">
              <i :class="showConfirmPassword ? 'pi pi-eye-slash' : 'pi pi-eye'"></i>
            </button>
          </div>
        </div>
        
        <button type="submit" class="btn btn-primary" :disabled="carregando">
          {{ carregando ? 'Cadastrando...' : 'Cadastrar' }}
        </button>
        
        <div v-if="erro" class="error-message">
          <i class="pi pi-exclamation-triangle"></i> {{ erro }}
        </div>
        
        <div v-if="sucesso" class="success-message">
          <i class="pi pi-check-circle"></i> {{ sucesso }}
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
    
    sucesso.value = 'Cadastro realizado com sucesso! Redirecionando...'
    
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
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: #F8FAFC;
}

/* Painel de destaque à esquerda */
.register-showcase {
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
  bottom: -140px;
  left: -120px;
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

.register-showcase h2 {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  max-width: 380px;
}

.register-showcase > p {
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

.register-card {
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
  .register-container {
    grid-template-columns: 1fr;
  }

  .register-showcase {
    display: none;
  }
}

.register-header {
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

.register-header h1 {
  font-size: 26px;
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
  font-weight: 600;
  font-size: 13px;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 16px 12px 44px;
  height: 44px;
  border: 1px solid #CBD5E1;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s ease;
  background: #F8FAFC;
  box-sizing: border-box;
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

.text-muted {
  font-size: 12px;
  color: #94A3B8;
  margin-top: 4px;
  display: block;
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

.success-message {
  margin-top: 16px;
  padding: 12px;
  background: #ECFDF5;
  color: #10B981;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  font-weight: 500;
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
  color: #4F46E5;
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