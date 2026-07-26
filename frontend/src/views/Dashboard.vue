<template>
  <div class="dashboard">
    <Navbar />

    <div class="container dashboard-content">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1>Olá, {{ authStore.getNome }}</h1>
          <p class="text-muted">Aqui está o resumo das suas finanças</p>
        </div>
        <button @click="carregarDados" class="btn btn-secondary btn-sm" :disabled="loading">
          <i class="pi pi-refresh" :class="{ 'pi-spin': loading }"></i>
          {{ loading ? 'Atualizando...' : 'Atualizar' }}
        </button>
      </div>

      <!-- Stats Grid -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="icon-badge icon-badge-success">
            <i class="pi pi-wallet"></i>
          </div>
          <div class="stat-content">
            <span class="stat-label">Saldo Total</span>
            <span class="stat-value" :class="saldoTotal >= 0 ? 'positive' : 'negative'">
              R$ {{ formatarValor(saldoTotal) }}
            </span>
          </div>
        </div>

        <div class="stat-card">
          <div class="icon-badge icon-badge-info">
            <i class="pi pi-arrow-up-right"></i>
          </div>
          <div class="stat-content">
            <span class="stat-label">Receitas</span>
            <span class="stat-value positive">R$ {{ formatarValor(receitas) }}</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="icon-badge icon-badge-danger">
            <i class="pi pi-arrow-down-right"></i>
          </div>
          <div class="stat-content">
            <span class="stat-label">Despesas</span>
            <span class="stat-value negative">R$ {{ formatarValor(despesas) }}</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="icon-badge icon-badge-warning">
            <i class="pi pi-chart-bar"></i>
          </div>
          <div class="stat-content">
            <span class="stat-label">Transações</span>
            <span class="stat-value">{{ transacoesStore.transacoes.length }}</span>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="charts-grid">
        <div class="card chart-card">
          <h3 class="card-title"><i class="pi pi-chart-line"></i> Receita x Despesa</h3>
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div v-for="mes in meses" :key="mes.label" class="bar-container">
                <div class="bar-wrapper">
                  <div
                    class="bar bar-receita"
                    :style="{ height: mes.receita + '%' }"
                    :title="'Receita: ' + mes.receita + '%'"
                  ></div>
                  <div
                    class="bar bar-despesa"
                    :style="{ height: mes.despesa + '%' }"
                    :title="'Despesa: ' + mes.despesa + '%'"
                  ></div>
                </div>
                <div class="bar-label">{{ mes.label }}</div>
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
                  :style="{ width: calcularPorcentagem(valor) + '%', background: getCategoryColor(categoria) }"
                ></div>
              </div>
              <span class="category-value">R$ {{ formatarValor(valor) }}</span>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-state-icon"><i class="pi pi-chart-pie"></i></div>
            <p class="empty-state-title">Nenhum dado ainda</p>
            <span class="empty-state-subtitle">Cadastre uma transação para visualizar os gráficos.</span>
            <router-link to="/transacoes" class="btn btn-primary btn-sm">
              <i class="pi pi-plus"></i> Nova Transação
            </router-link>
          </div>
        </div>
      </div>

      <!-- Bottom Row -->
      <div class="bottom-grid">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title"><i class="pi pi-clock"></i> Últimas Transações</h3>
            <router-link to="/transacoes" class="btn btn-secondary btn-sm">
              Ver todas
            </router-link>
          </div>

          <div v-if="transacoesStore.transacoes.length > 0" class="transaction-list">
            <div v-for="transacao in transacoesStore.ultimasTransacoes" :key="transacao.id" class="transaction-item">
              <div class="transaction-left">
                <div class="icon-badge" :class="transacao.tipo === 'RECEITA' ? 'icon-badge-success' : 'icon-badge-danger'">
                  <i :class="transacao.tipo === 'RECEITA' ? 'pi pi-arrow-up-right' : 'pi pi-arrow-down-right'"></i>
                </div>
                <div class="transaction-info">
                  <span class="transaction-desc">{{ transacao.descricao }}</span>
                  <span class="transaction-category">{{ transacao.categoria }} · {{ formatarData(transacao.data) }}</span>
                </div>
              </div>
              <div class="transaction-value" :class="transacao.tipo === 'RECEITA' ? 'positive' : 'negative'">
                {{ transacao.tipo === 'RECEITA' ? '+' : '-' }} R$ {{ formatarValor(transacao.valor) }}
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-state-icon"><i class="pi pi-file"></i></div>
            <p class="empty-state-title">Nenhuma transação cadastrada</p>
            <span class="empty-state-subtitle">Você ainda não possui transações neste período.</span>
            <router-link to="/transacoes" class="btn btn-primary btn-sm">
              <i class="pi pi-plus"></i> Adicionar
            </router-link>
          </div>
        </div>

        <div class="card">
          <h3 class="card-title"><i class="pi pi-flag"></i> Meta do Mês</h3>
          <div class="meta-card">
            <div class="meta-info">
              <span class="meta-title">Economia</span>
              <span class="meta-value">R$ 500 / R$ 1.000</span>
            </div>
            <div class="progress-track">
              <div class="progress-fill progress-fill-primary" style="width: 50%"></div>
            </div>
            <div class="meta-footer">
              <span class="progress-badge">50%</span>
            </div>
          </div>
          <div class="meta-card">
            <div class="meta-info">
              <span class="meta-title">Lazer</span>
              <span class="meta-value">R$ 200 / R$ 400</span>
            </div>
            <div class="progress-track">
              <div class="progress-fill progress-fill-warning" style="width: 50%"></div>
            </div>
            <div class="meta-footer">
              <span class="progress-badge">50%</span>
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
import { useTransacoesStore } from '../store/transacoes'
import Navbar from '../components/Navbar.vue'

const authStore = useAuthStore()
const transacoesStore = useTransacoesStore()
const loading = ref(false)

const saldoTotal = ref(0)
const receitas = ref(0)
const despesas = ref(0)
const categorias = ref({})

const meses = ref([
  { label: 'Jul', receita: 45, despesa: 25 },
  { label: 'Ago', receita: 55, despesa: 30 },
  { label: 'Set', receita: 35, despesa: 40 },
  { label: 'Out', receita: 65, despesa: 35 },
  { label: 'Nov', receita: 75, despesa: 20 },
  { label: 'Dez', receita: 50, despesa: 45 }
])

const formatarValor = (valor) => {
  if (valor === undefined || valor === null) return '0.00'
  return Number(valor).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, '.')
}

const formatarData = (data) => {
  if (!data) return ''
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR')
}

const calcularPorcentagem = (valor) => {
  if (despesas.value === 0 || !valor) return 0
  return (valor / despesas.value) * 100
}

const getCategoryColor = (categoria) => {
  const colors = {
    'Salário': '#3B82F6',
    'Alimentação': '#10B981',
    'Transporte': '#F59E0B',
    'Lazer': '#8B5CF6',
    'Estudos': '#EC4899',
    'Saúde': '#14B8A6',
    'Moradia': '#F97316',
    'Utilidades': '#6366F1'
  }
  return colors[categoria] || '#64748B'
}

const carregarDados = async () => {
  loading.value = true
  try {
    await transacoesStore.carregarTransacoes()
    const dadosSaldo = await transacoesStore.carregarSaldo()

    if (dadosSaldo) {
      saldoTotal.value = dadosSaldo.saldoTotal || 0
      receitas.value = dadosSaldo.receitas || 0
      despesas.value = dadosSaldo.despesas || 0
      categorias.value = dadosSaldo.categorias || {}
    }
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  carregarDados()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #F8FAFC;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.dashboard-content {
  padding: 30px 0;
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
}

.text-muted {
  color: #64748B;
  font-size: 14px;
  margin-top: 4px;
}

/* Stats Grid */
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

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #475569;
  font-weight: 600;
  display: block;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0F172A;
  display: block;
  margin-top: 2px;
}

.positive { color: #10B981; }
.negative { color: #EF4444; }

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
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
  width: 16px;
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
  font-size: 11px;
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

/* Categories */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
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
}

.category-value {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  min-width: 80px;
  text-align: right;
}

/* Bottom Grid */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

/* Transactions */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-header .card-title {
  margin-bottom: 0;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.transaction-item:hover {
  background: #F8FAFC;
}

.transaction-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.transaction-left .icon-badge {
  width: 36px;
  height: 36px;
  font-size: 14px;
}

.transaction-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.transaction-desc {
  font-weight: 600;
  color: #0F172A;
  font-size: 14px;
}

.transaction-category {
  font-size: 12px;
  color: #64748B;
}

.transaction-value {
  font-weight: 700;
  font-size: 14px;
  white-space: nowrap;
}

/* Meta */
.meta-card {
  padding: 16px;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  margin-bottom: 12px;
}

.meta-card:last-child {
  margin-bottom: 0;
}

.meta-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.meta-title {
  font-weight: 600;
  color: #0F172A;
  font-size: 14px;
}

.meta-value {
  font-size: 13px;
  color: #64748B;
}

.meta-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

/* Buttons */
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

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
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

  .bottom-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
