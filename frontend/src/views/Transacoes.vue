<template>
  <div class="transacoes">
    <Navbar />
    
    <div class="container">
      <div class="page-header">
        <div>
          <h1>💸 Transações</h1>
          <p class="text-muted">Gerencie todas as suas movimentações financeiras</p>
        </div>
        <div class="header-actions">
          <button @click="showModal = true" class="btn btn-primary">
            <i class="pi pi-plus"></i> Nova Transação
          </button>
          <button @click="triggerFileInput" class="btn btn-secondary">
            <i class="pi pi-upload"></i> Importar
          </button>
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileUpload" 
            accept=".csv,.ofx"
            style="display: none"
          >
        </div>
      </div>
      
      <div class="filters card">
        <div class="filter-group">
          <label>Tipo</label>
          <select v-model="filtro.tipo">
            <option value="">Todos</option>
            <option value="RECEITA">Receitas</option>
            <option value="DESPESA">Despesas</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Categoria</label>
          <select v-model="filtro.categoria">
            <option value="">Todas</option>
            <option v-for="cat in categorias" :key="cat" :value="cat">{{ cat }}</option>
          </select>
        </div>
        <div class="filter-group">
          <label>De</label>
          <input type="date" v-model="filtro.dataInicio">
        </div>
        <div class="filter-group">
          <label>Até</label>
          <input type="date" v-model="filtro.dataFim">
        </div>
        <button @click="aplicarFiltros" class="btn btn-secondary btn-sm">
          <i class="pi pi-filter"></i> Filtrar
        </button>
        <button @click="limparFiltros" class="btn btn-outline btn-sm">
          <i class="pi pi-times"></i> Limpar
        </button>
      </div>
      
      <div class="card">
        <div class="table-header">
          <span>{{ transacoesFiltradas.length }} transações</span>
          <span class="text-muted">
            Total: R$ {{ formatarValor(totalFiltrado) }}
          </span>
        </div>
        
        <div v-if="transacoesFiltradas.length > 0" class="table-container">
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Descrição</th>
                <th>Categoria</th>
                <th>Tipo</th>
                <th class="text-right">Valor</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="transacao in transacoesFiltradas" :key="transacao.id">
                <td>{{ formatarData(transacao.data) }}</td>
                <td>{{ transacao.descricao }}</td>
                <td><span class="badge-category">{{ transacao.categoria }}</span></td>
                <td>
                  <span class="badge" :class="transacao.tipo === 'RECEITA' ? 'badge-success' : 'badge-danger'">
                    {{ transacao.tipo }}
                  </span>
                </td>
                <td class="text-right" :class="transacao.tipo === 'RECEITA' ? 'positive' : 'negative'">
                  R$ {{ formatarValor(transacao.valor) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="empty-state">
          <span class="empty-icon">📭</span>
          <p>Nenhuma transação encontrada</p>
          <span class="empty-sub">Comece adicionando sua primeira transação</span>
          <button @click="showModal = true" class="btn btn-primary btn-sm">
            <i class="pi pi-plus"></i> Adicionar
          </button>
        </div>
      </div>
    </div>
    
    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>Nova Transação</h2>
          <button @click="showModal = false" class="close-btn">✕</button>
        </div>
        <form @submit.prevent="salvarTransacao" class="modal-body">
          <div class="form-group">
            <label>Descrição *</label>
            <input v-model="novaTransacao.descricao" required placeholder="Ex: Salário">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Valor *</label>
              <input 
                type="number" 
                v-model="novaTransacao.valor" 
                required 
                step="0.01"
                placeholder="0.00"
              >
            </div>
            <div class="form-group">
              <label>Data *</label>
              <input type="date" v-model="novaTransacao.data" required>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Categoria *</label>
              <input v-model="novaTransacao.categoria" required placeholder="Ex: Alimentação">
            </div>
            <div class="form-group">
              <label>Tipo *</label>
              <select v-model="novaTransacao.tipo" required>
                <option value="RECEITA">Receita</option>
                <option value="DESPESA">Despesa</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label>Observação</label>
            <textarea v-model="novaTransacao.observacao" rows="2" placeholder="Observações..."></textarea>
          </div>
          
          <div class="modal-footer">
            <button type="button" @click="showModal = false" class="btn btn-outline">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="salvando">
              {{ salvando ? 'Salvando...' : 'Salvar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useTransacoesStore } from '../store/transacoes'
import Navbar from '../components/Navbar.vue'
import Swal from 'sweetalert2'

const transacoesStore = useTransacoesStore()
const transacoes = ref([])
const showModal = ref(false)
const salvando = ref(false)
const fileInput = ref(null)

const filtro = ref({
  tipo: '',
  categoria: '',
  dataInicio: '',
  dataFim: ''
})

const novaTransacao = ref({
  descricao: '',
  valor: 0,
  data: new Date().toISOString().split('T')[0],
  categoria: '',
  tipo: 'DESPESA',
  observacao: ''
})

const categorias = computed(() => {
  const cats = new Set(transacoes.value.map(t => t.categoria))
  return Array.from(cats)
})

const transacoesFiltradas = computed(() => {
  let filtered = transacoes.value
  
  if (filtro.value.tipo) {
    filtered = filtered.filter(t => t.tipo === filtro.value.tipo)
  }
  if (filtro.value.categoria) {
    filtered = filtered.filter(t => t.categoria === filtro.value.categoria)
  }
  if (filtro.value.dataInicio) {
    filtered = filtered.filter(t => t.data >= filtro.value.dataInicio)
  }
  if (filtro.value.dataFim) {
    filtered = filtered.filter(t => t.data <= filtro.value.dataFim)
  }
  
  return filtered
})

const totalFiltrado = computed(() => {
  return transacoesFiltradas.value.reduce((sum, t) => {
    return t.tipo === 'RECEITA' ? sum + t.valor : sum - t.valor
  }, 0)
})

const formatarValor = (valor) => {
  return Number(valor).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, '.')
}

const formatarData = (data) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR')
}

const carregarTransacoes = async () => {
  try {
    transacoes.value = await transacoesStore.carregarTransacoes()
  } catch (error) {
    Swal.fire('Erro', 'Erro ao carregar transações', 'error')
  }
}

const salvarTransacao = async () => {
  salvando.value = true
  try {
    await transacoesStore.criarTransacao(novaTransacao.value)
    await carregarTransacoes()
    showModal.value = false
    novaTransacao.value = {
      descricao: '',
      valor: 0,
      data: new Date().toISOString().split('T')[0],
      categoria: '',
      tipo: 'DESPESA',
      observacao: ''
    }
    Swal.fire({
      icon: 'success',
      title: 'Sucesso!',
      text: 'Transação criada com sucesso!',
      timer: 2000,
      showConfirmButton: false
    })
  } catch (error) {
    Swal.fire('Erro', 'Erro ao criar transação', 'error')
  } finally {
    salvando.value = false
  }
}

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    const result = await transacoesStore.importarArquivo(file)
    await carregarTransacoes()
    Swal.fire('Sucesso', result.mensagem || 'Arquivo importado com sucesso!', 'success')
  } catch (error) {
    Swal.fire('Erro', error.response?.data?.erro || 'Erro ao importar arquivo', 'error')
  } finally {
    fileInput.value.value = ''
  }
}

const aplicarFiltros = () => {}
const limparFiltros = () => {
  filtro.value = {
    tipo: '',
    categoria: '',
    dataInicio: '',
    dataFim: ''
  }
}

onMounted(() => {
  carregarTransacoes()
})
</script>

<style scoped>
.transacoes {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC, #F1F5F9);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.transacoes .container {
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
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-group label {
  font-size: 12px;
  color: #64748B;
  font-weight: 500;
}

.filter-group select,
.filter-group input {
  padding: 8px 12px;
  height: 40px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 14px;
  background: #F8FAFC;
  min-width: 140px;
}

.filter-group select:focus,
.filter-group input:focus {
  outline: none;
  border-color: #2563EB;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #64748B;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead tr {
  background: #F8FAFC;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #0F172A;
  border-bottom: 2px solid #E2E8F0;
  font-size: 13px;
}

tbody tr {
  transition: background 0.2s ease;
}

tbody tr:nth-child(even) {
  background: #F8FAFC;
}

tbody tr:hover {
  background: #EFF6FF;
}

td {
  padding: 12px 16px;
  border-bottom: 1px solid #F1F5F9;
  font-size: 14px;
  color: #0F172A;
}

.text-right {
  text-align: right;
}

.badge-category {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: #F1F5F9;
  color: #64748B;
}

.badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.badge-success {
  background: #22C55E20;
  color: #22C55E;
}

.badge-danger {
  background: #EF444420;
  color: #EF4444;
}

.positive { color: #22C55E; }
.negative { color: #EF4444; }

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

.btn-outline {
  background: transparent;
  border: 2px solid #E2E8F0;
  color: #64748B;
}

.btn-outline:hover {
  border-color: #2563EB;
  color: #2563EB;
}

.btn-sm {
  padding: 6px 14px;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #64748B;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.empty-state p {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.empty-sub {
  font-size: 14px;
  color: #94A3B8;
  display: block;
  margin-bottom: 16px;
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
  border-bottom: 1px solid #E2E8F0;
}

.modal-header h2 {
  font-size: 20px;
  color: #0F172A;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #64748B;
  cursor: pointer;
  padding: 4px;
}

.close-btn:hover {
  color: #0F172A;
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

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #0F172A;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  font-size: 14px;
  background: #F8FAFC;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2563EB;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
  background: white;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    flex-direction: column;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-group select,
  .filter-group input {
    min-width: auto;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal {
    width: 95%;
  }
}
</style>