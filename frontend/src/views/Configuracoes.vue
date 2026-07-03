<template>
  <div class="configuracoes">
    <Navbar />
    
    <div class="container">
      <div class="page-header">
        <div>
          <h1>⚙️ Configurações</h1>
          <p class="text-muted">Gerencie suas preferências e configurações</p>
        </div>
      </div>

      <div class="settings-grid">
        <!-- Perfil -->
        <div class="card">
          <h3>👤 Perfil</h3>
          <div class="settings-form">
            <div class="form-group">
              <label>Nome</label>
              <input v-model="perfil.nome" placeholder="Seu nome">
            </div>
            <div class="form-group">
              <label>Email</label>
              <input v-model="perfil.email" placeholder="seu@email.com" disabled>
              <small class="text-muted">Email não pode ser alterado</small>
            </div>
            <button class="btn btn-primary" @click="salvarPerfil">
              <i class="pi pi-save"></i> Salvar Perfil
            </button>
          </div>
        </div>

        <!-- Segurança -->
        <div class="card">
          <h3>🔒 Segurança</h3>
          <div class="settings-form">
            <div class="form-group">
              <label>Senha Atual</label>
              <input type="password" v-model="seguranca.senhaAtual" placeholder="••••••••">
            </div>
            <div class="form-group">
              <label>Nova Senha</label>
              <input type="password" v-model="seguranca.novaSenha" placeholder="••••••••">
            </div>
            <div class="form-group">
              <label>Confirmar Nova Senha</label>
              <input type="password" v-model="seguranca.confirmarSenha" placeholder="••••••••">
            </div>
            <button class="btn btn-primary" @click="alterarSenha">
              <i class="pi pi-key"></i> Alterar Senha
            </button>
          </div>
        </div>

        <!-- Preferências -->
        <div class="card">
          <h3>🎨 Preferências</h3>
          <div class="settings-form">
            <div class="form-group">
              <label>Moeda</label>
              <select v-model="preferencias.moeda">
                <option value="BRL">Real (R$)</option>
                <option value="USD">Dólar (US$)</option>
                <option value="EUR">Euro (€)</option>
              </select>
            </div>
            <div class="form-group">
              <label>Idioma</label>
              <select v-model="preferencias.idioma">
                <option value="pt">Português</option>
                <option value="en">English</option>
                <option value="es">Español</option>
              </select>
            </div>
            <div class="form-group">
              <label>Formato de Data</label>
              <select v-model="preferencias.formatoData">
                <option value="dd/mm/yyyy">DD/MM/YYYY</option>
                <option value="mm/dd/yyyy">MM/DD/YYYY</option>
                <option value="yyyy/mm/dd">YYYY/MM/DD</option>
              </select>
            </div>
            <button class="btn btn-primary" @click="salvarPreferencias">
              <i class="pi pi-save"></i> Salvar Preferências
            </button>
          </div>
        </div>

        <!-- Notificações -->
        <div class="card">
          <h3>🔔 Notificações</h3>
          <div class="settings-form">
            <div class="toggle-group">
              <div class="toggle-item">
                <span>Alertas de gastos</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="notificacoes.alertasGastos">
                  <span class="toggle-slider"></span>
                </label>
              </div>
              <div class="toggle-item">
                <span>Lembretes de metas</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="notificacoes.lembretesMetas">
                  <span class="toggle-slider"></span>
                </label>
              </div>
              <div class="toggle-item">
                <span>Relatórios semanais</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="notificacoes.relatoriosSemanais">
                  <span class="toggle-slider"></span>
                </label>
              </div>
              <div class="toggle-item">
                <span>Promoções e novidades</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="notificacoes.promocoes">
                  <span class="toggle-slider"></span>
                </label>
              </div>
            </div>
            <button class="btn btn-primary" @click="salvarNotificacoes">
              <i class="pi pi-save"></i> Salvar Notificações
            </button>
          </div>
        </div>

        <!-- Dados -->
        <div class="card">
          <h3>📁 Dados</h3>
          <div class="settings-form">
            <div class="button-group">
              <button class="btn btn-secondary" @click="exportarDados">
                <i class="pi pi-download"></i> Exportar Dados
              </button>
              <button class="btn btn-secondary" @click="importarDados">
                <i class="pi pi-upload"></i> Importar Dados
              </button>
            </div>
            <div class="danger-zone">
              <h4>⚠️ Zona de Perigo</h4>
              <button class="btn btn-danger" @click="excluirDados">
                <i class="pi pi-trash"></i> Excluir Todos os Dados
              </button>
            </div>
          </div>
        </div>

        <!-- Sobre -->
        <div class="card">
          <h3>ℹ️ Sobre</h3>
          <div class="settings-form">
            <p><strong>Finanças Pessoais v1.0.0</strong></p>
            <p>Sistema de controle financeiro pessoal</p>
            <p class="text-muted">Desenvolvido com ❤️ para FinTech</p>
            <div class="about-links">
              <a href="#" class="link">📖 Documentação</a>
              <a href="#" class="link">🐛 Reportar Bug</a>
              <a href="#" class="link">💡 Sugerir Melhoria</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import Navbar from '../components/Navbar.vue'
import Swal from 'sweetalert2'

const authStore = useAuthStore()

const perfil = ref({
  nome: '',
  email: ''
})

const seguranca = ref({
  senhaAtual: '',
  novaSenha: '',
  confirmarSenha: ''
})

const preferencias = ref({
  moeda: 'BRL',
  idioma: 'pt',
  formatoData: 'dd/mm/yyyy'
})

const notificacoes = ref({
  alertasGastos: true,
  lembretesMetas: true,
  relatoriosSemanais: false,
  promocoes: false
})

const salvarPerfil = () => {
  Swal.fire({
    icon: 'success',
    title: 'Perfil atualizado!',
    text: 'Suas informações foram salvas com sucesso.',
    timer: 2000,
    showConfirmButton: false
  })
}

const alterarSenha = () => {
  if (seguranca.value.novaSenha !== seguranca.value.confirmarSenha) {
    Swal.fire('Erro', 'As senhas não coincidem', 'error')
    return
  }
  if (seguranca.value.novaSenha.length < 6) {
    Swal.fire('Erro', 'A senha deve ter no mínimo 6 caracteres', 'error')
    return
  }
  
  Swal.fire({
    icon: 'success',
    title: 'Senha alterada!',
    text: 'Sua senha foi atualizada com sucesso.',
    timer: 2000,
    showConfirmButton: false
  })
  
  seguranca.value = {
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: ''
  }
}

const salvarPreferencias = () => {
  Swal.fire({
    icon: 'success',
    title: 'Preferências salvas!',
    timer: 2000,
    showConfirmButton: false
  })
}

const salvarNotificacoes = () => {
  Swal.fire({
    icon: 'success',
    title: 'Notificações salvas!',
    timer: 2000,
    showConfirmButton: false
  })
}

const exportarDados = () => {
  Swal.fire({
    icon: 'info',
    title: 'Exportação de Dados',
    text: 'Funcionalidade em desenvolvimento',
    confirmButtonText: 'OK'
  })
}

const importarDados = () => {
  Swal.fire({
    icon: 'info',
    title: 'Importação de Dados',
    text: 'Funcionalidade em desenvolvimento',
    confirmButtonText: 'OK'
  })
}

const excluirDados = () => {
  Swal.fire({
    title: '⚠️ Atenção!',
    text: 'Tem certeza que deseja excluir todos os seus dados? Esta ação não pode ser desfeita.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#EF4444',
    cancelButtonColor: '#64748B',
    confirmButtonText: 'Sim, excluir tudo!',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      Swal.fire({
        icon: 'success',
        title: 'Dados excluídos!',
        text: 'Todos os dados foram removidos com sucesso.',
        timer: 2000,
        showConfirmButton: false
      })
    }
  })
}

onMounted(() => {
  perfil.value.nome = authStore.getNome || ''
  perfil.value.email = authStore.getEmail || ''
})
</script>

<style scoped>
.configuracoes {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC, #F1F5F9);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.configuracoes .container {
  padding: 30px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  color: #0F172A;
  font-weight: 700;
}

.text-muted {
  color: #64748B;
  font-size: 14px;
  margin-top: 4px;
}

.settings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
}

.card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 20px;
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-group label {
  font-weight: 500;
  color: #0F172A;
  font-size: 14px;
}

.form-group input,
.form-group select {
  padding: 10px 12px;
  height: 44px;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  font-size: 14px;
  background: #F8FAFC;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #2563EB;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
  background: white;
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-group .text-muted {
  font-size: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.btn-primary {
  background: #2563EB;
  color: white;
}

.btn-primary:hover {
  background: #1D4ED8;
  transform: scale(1.02);
}

.btn-secondary {
  background: #F1F5F9;
  color: #0F172A;
}

.btn-secondary:hover {
  background: #E2E8F0;
  transform: scale(1.02);
}

.btn-danger {
  background: #EF4444;
  color: white;
}

.btn-danger:hover {
  background: #DC2626;
  transform: scale(1.02);
}

.button-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.danger-zone {
  margin-top: 16px;
  padding: 16px;
  border: 2px solid #FEE2E2;
  border-radius: 10px;
  background: #FEF2F2;
}

.danger-zone h4 {
  color: #DC2626;
  margin-bottom: 12px;
  font-size: 14px;
}

/* Toggle Switch */
.toggle-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toggle-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.toggle-switch {
  position: relative;
  width: 48px;
  height: 24px;
  cursor: pointer;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #CBD5E1;
  border-radius: 24px;
  transition: 0.3s ease;
}

.toggle-slider:before {
  content: "";
  position: absolute;
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background: white;
  border-radius: 50%;
  transition: 0.3s ease;
}

.toggle-switch input:checked + .toggle-slider {
  background: #2563EB;
}

.toggle-switch input:checked + .toggle-slider:before {
  transform: translateX(24px);
}

.about-links {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.link {
  color: #2563EB;
  text-decoration: none;
  font-size: 14px;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 1024px) {
  .settings-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .settings-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .button-group {
    flex-direction: column;
  }
}
</style>