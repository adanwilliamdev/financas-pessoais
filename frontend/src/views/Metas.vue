<template>
  <div class="metas">
    <Navbar />

    <div class="container">
      <div class="page-header">
        <h1><i class="pi pi-flag"></i> Metas de Economia</h1>
        <button @click="showModal = true" class="btn btn-primary">
          <i class="pi pi-plus"></i> Nova Meta
        </button>
      </div>

      <div v-if="metas.length > 0" class="metas-grid">
        <div v-for="meta in metas" :key="meta.id" class="meta-card">
          <div class="meta-header">
            <span class="meta-categoria">{{ meta.categoria }}</span>
            <span class="tag" :class="meta.ativo ? 'tag-info' : 'tag-success'">
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
            <div class="progress-track">
              <div class="progress-fill progress-fill-primary" :style="{ width: meta.porcentagemConclusao + '%' }"></div>
            </div>
            <span class="progress-badge">{{ meta.porcentagemConclusao.toFixed(0) }}%</span>
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
        <div class="empty-state-icon"><i class="pi pi-flag"></i></div>
        <p class="empty-state-title">Nenhuma meta criada</p>
        <span class="empty-state-subtitle">Defina metas de economia por categoria e acompanhe seu progresso.</span>
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
    await api.put(`/metas/${id}/progresso`)
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
  background: #F8FAFC;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.page-header h1 {
  font-size: 26px;
  color: #0F172A;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-header h1 i {
  color: #4F46E5;
  font-size: 22px;
}

.metas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.meta-card {
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 1px 3px 0 rgba(0,0,0,0.05), 0 1px 2px 0 rgba(0,0,0,0.02);
  transition: all 0.2s ease;
}

.meta-card:hover {
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.meta-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.meta-categoria {
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.tag-info { background: #EFF6FF; color: #3B82F6; }
.tag-success { background: #ECFDF5; color: #10B981; }

.meta-valores {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-valores .label {
  display: block;
  font-size: 12px;
  color: #64748B;
  margin-bottom: 4px;
}

.meta-valores .valor {
  font-size: 15px;
  font-weight: 700;
  color: #0F172A;
}

.positive { color: #10B981; }
.negative { color: #EF4444; }

.meta-progresso {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.progress-track {
  flex: 1;
  height: 10px;
  background: #F1F5F9;
  border-radius: 999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 999px;
  transition: width 0.6s ease;
}

.progress-fill-primary {
  background: #4F46E5;
}

.progress-badge {
  font-size: 12px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 999px;
  background: #EEF2FF;
  color: #4F46E5;
  min-width: 40px;
  text-align: center;
}

.meta-periodo {
  font-size: 13px;
  color: #64748B;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-block {
  width: 100%;
  justify-content: center;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 72px 20px;
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  box-shadow: 0 1px 3px 0 rgba(0,0,0,0.05), 0 1px 2px 0 rgba(0,0,0,0.02);
}

.empty-state-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: #F1F5F9;
  color: #94A3B8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 16px;
}

.empty-state-title {
  font-size: 16px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 4px;
}

.empty-state-subtitle {
  font-size: 13px;
  color: #64748B;
  max-width: 340px;
  margin-bottom: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
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
  animation: slideUp 0.25s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
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
  border-bottom: 1px solid #E2E8F0;
}

.modal-header h2 {
  font-size: 18px;
  font-weight: 700;
  color: #0F172A;
}

.close-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: #64748B;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.15s ease;
}

.close-btn:hover {
  color: #0F172A;
  background: #F1F5F9;
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
  border-top: 1px solid #E2E8F0;
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
  font-weight: 600;
  color: #475569;
  font-size: 13px;
}

.form-group input {
  width: 100%;
  height: 42px;
  padding: 0 12px;
  border: 1px solid #CBD5E1;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #4F46E5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: #4F46E5;
  color: white;
}

.btn-primary:hover {
  background: #4338CA;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25);
}

.btn-secondary {
  background: #F1F5F9;
  color: #0F172A;
}

.btn-secondary:hover {
  background: #E2E8F0;
}

.btn-outline {
  background: transparent;
  border: 1.5px solid #E2E8F0;
  color: #475569;
}

.btn-outline:hover {
  border-color: #4F46E5;
  color: #4F46E5;
}

.btn-sm {
  padding: 6px 14px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
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
