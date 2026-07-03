<template>
  <div class="metas">
    <Navbar />
    
    <div class="container">
      <div class="page-header">
        <h1>🎯 Metas de Economia</h1>
        <button @click="showModal = true" class="btn btn-primary">
          <i class="pi pi-plus"></i> Nova Meta
        </button>
      </div>
      
      <div v-if="metas.length > 0" class="metas-grid">
        <div v-for="meta in metas" :key="meta.id" class="meta-card">
          <div class="meta-header">
            <span class="meta-categoria">{{ meta.categoria }}</span>
            <span class="meta-status" :class="meta.ativo ? 'status-ativo' : 'status-concluido'">
              {{ meta.ativo ? 'Em andamento' : 'Concluída' }}
            </span>
          </div>
          
          <div class="meta-valores">
            <div>
              <span class="label">Meta</span>
              <span class="valor">R$ {{ formatarValor(meta.valorMeta) }}</span>
            </div>
            <div>
              <span class="label">Gasto</span>
              <span class="valor">R$ {{ formatarValor(meta.valorAtual || 0) }}</span>
            </div>
            <div>
              <span class="label">Restante</span>
              <span class="valor" :class="meta.valorMeta - meta.valorAtual > 0 ? 'positive' : 'negative'">
                R$ {{ formatarValor(meta.valorMeta - (meta.valorAtual || 0)) }}
              </span>
            </div>
          </div>
          
          <div class="meta-progresso">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: meta.porcentagemConclusao + '%' }"></div>
            </div>
            <span class="progress-text">{{ meta.porcentagemConclusao.toFixed(1) }}%</span>
          </div>
          
          <div class="meta-periodo">
            <i class="pi pi-calendar"></i>
            {{ formatarData(meta.dataInicio) }} - {{ formatarData(meta.dataFim) }}
          </div>
          
          <button @click="atualizarProgresso(meta.id)" class="btn btn-secondary btn-sm btn-block">
            <i class="pi pi-refresh"></i> Atualizar Progresso
          </button>
        </div>
      </div>
      
      <div v-else class="empty-state">
        <i class="pi pi-flag"></i>
        <p>Nenhuma meta criada</p>
        <button @click="showModal = true" class="btn btn-primary">
          <i class="pi pi-plus"></i> Criar sua primeira meta
        </button>
      </div>
    </div>
    
    <!-- Modal Nova Meta -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>Nova Meta de Economia</h2>
          <button @click="showModal = false" class="close-btn">
            <i class="pi pi-times"></i>
          </button>
        </div>
        <form @submit.prevent="salvarMeta" class="modal-body">
          <div class="form-group">
            <label>Categoria *</label>
            <input v-model="novaMeta.categoria" required placeholder="Ex: Lazer">
          </div>
          <div class="form-group">
            <label>Valor Meta *</label>
            <input 
              type="number" 
              v-model="novaMeta.valorMeta" 
              required 
              step="0.01"
              placeholder="0.00"
            >
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Data Início *</label>
              <input type="date" v-model="novaMeta.dataInicio" required>
            </div>
            <div class="form-group">
              <label>Data Fim *</label>
              <input type="date" v-model="novaMeta.dataFim" required>
            </div>
          </div>
          
          <div class="modal-footer">
            <button type="button" @click="showModal = false" class="btn btn-outline">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="salvando">
              <i v-if="salvando" class="pi pi-spin pi-spinner"></i>
              {{ salvando ? 'Salvando...' : 'Salvar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/api'
import Navbar from '../components/Navbar.vue'
import Swal from 'sweetalert2'

const metas = ref([])
const showModal = ref(false)
const salvando = ref(false)

const novaMeta = ref({
  categoria: '',
  valorMeta: 0,
  dataInicio: new Date().toISOString().split('T')[0],
  dataFim: ''
})

const formatarValor = (valor) => {
  return Number(valor).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, '.')
}

const formatarData = (data) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR')
}

const carregarMetas = async () => {
  try {
    const response = await api.get('/metas')
    metas.value = response.data
  } catch (error) {
    console.error('Erro ao carregar metas:', error)
  }
}

const salvarMeta = async () => {
  salvando.value = true
  try {
    await api.post('/metas', novaMeta.value)
    await carregarMetas()
    showModal.value = false
    novaMeta.value = {
      categoria: '',
      valorMeta: 0,
      dataInicio: new Date().toISOString().split('T')[0],
      dataFim: ''
    }
    Swal.fire('Sucesso', 'Meta criada com sucesso!', 'success')
  } catch (error) {
    Swal.fire('Erro', 'Erro ao criar meta', 'error')
  } finally {
    salvando.value = false
  }
}

const atualizarProgresso = async (id) => {
  try {
    await api.put(/metas//progresso)
    await carregarMetas()
    Swal.fire('Sucesso', 'Progresso atualizado!', 'success')
  } catch (error) {
    Swal.fire('Erro', 'Erro ao atualizar progresso', 'error')
  }
}

onMounted(() => {
  carregarMetas()
})
</script>

<style scoped>
.metas {
  min-height: 100vh;
  background: #f0f2f5;
}

.container {
  padding: 30px 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
}

.metas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.meta-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
}

.meta-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.meta-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.meta-categoria {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.meta-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
}

.status-ativo {
  background: #e3f2fd;
  color: #2196f3;
}

.status-concluido {
  background: #e8f5e9;
  color: #4caf50;
}

.meta-valores {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-valores .label {
  display: block;
  font-size: 12px;
  color: #888;
  margin-bottom: 4px;
}

.meta-valores .valor {
  font-size: 16px;
  font-weight: 600;
}

.meta-progresso {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.6s ease;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  min-width: 48px;
  text-align: right;
}

.meta-periodo {
  font-size: 13px;
  color: #888;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state p {
  color: #888;
  margin-bottom: 20px;
  font-size: 18px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: white;
  border-radius: 16px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease;
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

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h2 {
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #888;
  cursor: pointer;
  padding: 4px;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .metas-grid {
    grid-template-columns: 1fr;
  }
  
  .meta-valores {
    grid-template-columns: 1fr 1fr 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal {
    width: 95%;
  }
}
</style>
