<template>
  <div class="relatorios">
    <Navbar />

    <div class="container">
      <div class="page-header">
        <div>
          <h1><i class="pi pi-chart-bar"></i> Relatórios</h1>
          <p class="text-muted">Análise detalhada das suas finanças</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary btn-sm" @click="exportarPDF">
            <i class="pi pi-file-pdf"></i> Exportar PDF
          </button>
          <button class="btn btn-secondary btn-sm" @click="exportarExcel">
            <i class="pi pi-file-excel"></i> Exportar Excel
          </button>
        </div>
      </div>

      <!-- Filtros -->
      <div class="filters card">
        <div class="filter-group">
          <label>Período</label>
          <select v-model="periodo">
            <option value="mes">Este Mês</option>
            <option value="trimestre">Último Trimestre</option>
            <option value="semestre">Último Semestre</option>
            <option value="ano">Este Ano</option>
            <option value="personalizado">Personalizado</option>
          </select>
        </div>
        <div class="filter-group" v-if="periodo === 'personalizado'">
          <label>Data Início</label>
          <input type="date" v-model="dataInicio">
        </div>
        <div class="filter-group" v-if="periodo === 'personalizado'">
          <label>Data Fim</label>
          <input type="date" v-model="dataFim">
        </div>
        <button class="btn btn-primary btn-sm" @click="aplicarFiltro">
          <i class="pi pi-search"></i> Aplicar
        </button>
      </div>

      <!-- Resumo -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="icon-badge icon-badge-info"><i class="pi pi-arrow-up-right"></i></div>
          <div>
            <div class="stat-label">Total Receitas</div>
            <div class="stat-value positive">R$ {{ formatarValor(totalReceitas) }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="icon-badge icon-badge-danger"><i class="pi pi-arrow-down-right"></i></div>
          <div>
            <div class="stat-label">Total Despesas</div>
            <div class="stat-value negative">R$ {{ formatarValor(totalDespesas) }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="icon-badge icon-badge-success"><i class="pi pi-wallet"></i></div>
          <div>
            <div class="stat-label">Saldo do Período</div>
            <div class="stat-value" :class="saldoPeriodo >= 0 ? 'positive' : 'negative'">
              R$ {{ formatarValor(saldoPeriodo) }}
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="icon-badge icon-badge-warning"><i class="pi pi-list"></i></div>
          <div>
            <div class="stat-label">Transações</div>
            <div class="stat-value">{{ transacoes.length }}</div>
          </div>
        </div>
      </div>

      <!-- Gráficos -->
      <div class="charts-grid">
        <div class="card chart-card">
          <h3 class="card-title"><i class="pi pi-chart-line"></i> Evolução Mensal</h3>
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div v-for="(mes, index) in meses" :key="index" class="bar-container">
                <div class="bar-wrapper">
                  <div class="bar bar-receita" :style="{ height: getReceitaMes(index) + '%' }"></div>
                  <div class="bar bar-despesa" :style="{ height: getDespesaMes(index) + '%' }"></div>
                </div>
                <div class="bar-label">{{ mes }}</div>
              </div>
            </div>
            <div class="chart-legend">
              <span><span class="legend-dot" style="background: #3B82F6;"></span> Receitas</span>
              <span><span class="legend-dot" style="background: #EF4444;"></span> Despesas</span>
            </div>
          </div>
        </div>

        <div class="card chart-card">
          <h3 class="card-title"><i class="pi pi-chart-pie"></i> Despesas por Categoria</h3>
          <div v-if="Object.keys(categorias).length > 0" class="category-list">
            <div v-for="(valor, categoria) in categorias" :key="categoria" class="category-item">
              <span class="category-name">{{ categoria }}</span>
              <div class="category-bar-container">
                <div
                  class="category-bar"
                  :style="{ width: calcularPorcentagemCategoria(valor) + '%' }"
                ></div>
              </div>
              <span class="category-value">R$ {{ formatarValor(valor) }}</span>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-state-icon"><i class="pi pi-chart-pie"></i></div>
            <p class="empty-state-title">Sem dados para este período</p>
          </div>
        </div>
      </div>

      <!-- Top Categorias -->
      <div class="card">
        <h3 class="card-title"><i class="pi pi-star"></i> Top Categorias</h3>
        <div v-if="topCategorias.length > 0" class="top-categories">
          <div v-for="(item, index) in topCategorias" :key="index" class="top-item">
            <span class="top-position">#{{ index + 1 }}</span>
            <span class="top-name">{{ item.categoria }}</span>
            <div class="top-bar-container">
              <div class="top-bar" :style="{ width: item.porcentagem + '%' }"></div>
            </div>
            <span class="top-value negative">R$ {{ formatarValor(item.total) }}</span>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-state-icon"><i class="pi pi-star"></i></div>
          <p class="empty-state-title">Sem dados para exibir</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useTransacoesStore } from '../store/transacoes'
import Navbar from '../components/Navbar.vue'

const transacoesStore = useTransacoesStore()
const transacoes = ref([])
const periodo = ref('mes')
const dataInicio = ref('')
const dataFim = ref('')

const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

const totalReceitas = computed(() => {
  return transacoes.value
    .filter(t => t.tipo === 'RECEITA')
    .reduce((sum, t) => sum + t.valor, 0)
})

const totalDespesas = computed(() => {
  return transacoes.value
    .filter(t => t.tipo === 'DESPESA')
    .reduce((sum, t) => sum + t.valor, 0)
})

const saldoPeriodo = computed(() => {
  return totalReceitas.value - totalDespesas.value
})

const categorias = computed(() => {
  const cats = {}
  transacoes.value
    .filter(t => t.tipo === 'DESPESA')
    .forEach(t => {
      cats[t.categoria] = (cats[t.categoria] || 0) + t.valor
    })
  return cats
})

const topCategorias = computed(() => {
  const items = Object.entries(categorias.value).map(([categoria, total]) => ({
    categoria,
    total,
    porcentagem: totalDespesas.value > 0 ? (total / totalDespesas.value) * 100 : 0
  }))
  return items.sort((a, b) => b.total - a.total).slice(0, 5)
})

const formatarValor = (valor) => {
  if (valor === undefined || valor === null) return '0.00'
  return Number(valor).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, '.')
}

const calcularPorcentagemCategoria = (valor) => {
  if (totalDespesas.value === 0 || !valor) return 0
  return (valor / totalDespesas.value) * 100
}

const getReceitaMes = (index) => {
  // Simulação - em produção viria do backend
  return 30 + Math.random() * 60
}

const getDespesaMes = (index) => {
  return 20 + Math.random() * 50
}

const aplicarFiltro = async () => {
  await carregarDados()
}

const carregarDados = async () => {
  try {
    const dados = await transacoesStore.carregarTransacoes()
    transacoes.value = dados || []
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  }
}

const exportarPDF = () => {
  alert('Funcionalidade de exportação PDF em desenvolvimento')
}

const exportarExcel = () => {
  alert('Funcionalidade de exportação Excel em desenvolvimento')
}

onMounted(() => {
  carregarDados()
})
</script>

<style scoped>
.relatorios {
  min-height: 100vh;
  background: #F8FAFC;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.relatorios .container {
  padding: 30px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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

.text-muted {
  color: #64748B;
  font-size: 14px;
  margin-top: 4px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px 0 rgba(0,0,0,0.05), 0 1px 2px 0 rgba(0,0,0,0.02);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: #4F46E5;
  font-size: 14px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-group label {
  font-size: 12px;
  color: #475569;
  font-weight: 600;
}

.filter-group select,
.filter-group input {
  padding: 0 12px;
  height: 42px;
  border: 1px solid #CBD5E1;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  background: white;
  min-width: 140px;
  transition: all 0.2s ease;
}

.filter-group select:focus,
.filter-group input:focus {
  outline: none;
  border-color: #4F46E5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px 0 rgba(0,0,0,0.05), 0 1px 2px 0 rgba(0,0,0,0.02);
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.icon-badge {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 17px;
}

.icon-badge-success { background: #ECFDF5; color: #10B981; }
.icon-badge-danger  { background: #FEF2F2; color: #EF4444; }
.icon-badge-info    { background: #EFF6FF; color: #3B82F6; }
.icon-badge-warning { background: #FFFBEB; color: #F59E0B; }

.stat-label {
  font-size: 13px;
  color: #475569;
  font-weight: 600;
  display: block;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #0F172A;
}

.positive { color: #10B981; }
.negative { color: #EF4444; }

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  min-height: 300px;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 140px;
  gap: 6px;
}

.bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.bar-wrapper {
  display: flex;
  gap: 3px;
  align-items: flex-end;
  height: 120px;
  width: 100%;
  justify-content: center;
}

.bar {
  width: 14px;
  border-radius: 6px 6px 0 0;
  transition: height 0.6s ease;
  min-height: 4px;
}

.bar-receita {
  background: linear-gradient(180deg, #3B82F6 0%, rgba(59, 130, 246, 0.5) 100%);
}

.bar-despesa {
  background: linear-gradient(180deg, #EF4444 0%, rgba(239, 68, 68, 0.5) 100%);
}

.bar-label {
  font-size: 10px;
  color: #64748B;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  font-size: 13px;
  color: #64748B;
}

.legend-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-name {
  min-width: 80px;
  font-size: 14px;
  color: #0F172A;
  font-weight: 500;
}

.category-bar-container {
  flex: 1;
  height: 8px;
  background: #F1F5F9;
  border-radius: 999px;
  overflow: hidden;
}

.category-bar {
  height: 100%;
  border-radius: 999px;
  transition: width 0.8s ease;
  background: linear-gradient(90deg, #4F46E5, #3B82F6);
}

.category-value {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  min-width: 80px;
  text-align: right;
}

.top-categories {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: #F8FAFC;
  border-radius: 12px;
}

.top-position {
  font-weight: 700;
  color: #94A3B8;
  min-width: 26px;
  font-size: 13px;
}

.top-name {
  flex: 1;
  font-weight: 600;
  color: #0F172A;
  font-size: 14px;
}

.top-value {
  font-weight: 700;
  min-width: 90px;
  text-align: right;
  font-size: 14px;
}

.top-bar-container {
  width: 100px;
  height: 8px;
  background: #E2E8F0;
  border-radius: 999px;
  overflow: hidden;
}

.top-bar {
  height: 100%;
  background: linear-gradient(90deg, #EF4444, rgba(239, 68, 68, 0.6));
  border-radius: 999px;
  transition: width 0.8s ease;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px 20px;
}

.empty-state-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: #F1F5F9;
  color: #94A3B8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.empty-state-title {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
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

.btn-sm {
  padding: 6px 14px;
  font-size: 12px;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .filters {
    flex-direction: column;
  }

  .filter-group select,
  .filter-group input {
    min-width: auto;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
