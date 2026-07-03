<template>
  <div class="dashboard">
    <Navbar />
    
    <div class="container dashboard-content">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1>Olá, {{ authStore.getNome }} 👋</h1>
          <p class="text-muted">Aqui está o resumo das suas finanças</p>
        </div>
        <button @click="carregarDados" class="btn btn-secondary btn-sm" :disabled="loading">
          <i class="pi pi-refresh" :class="{ 'pi-spin': loading }"></i> 
          {{ loading ? 'Atualizando...' : 'Atualizar' }}
        </button>
      </div>

      <!-- Stats Grid Premium -->
      <div class="stats-grid">
        <div class="stat-card stat-saldo">
          <div class="stat-icon-wrapper">
            <div class="stat-icon" style="background: linear-gradient(135deg, #22C55E, #16A34A);">
              <span>💰</span>
            </div>
          </div>
          <div class="stat-content">
            <span class="stat-label">Saldo Total</span>
            <span class="stat-value" :class="saldoTotal >= 0 ? 'positive' : 'negative'">
              R$ {{ formatarValor(saldoTotal) }}
            </span>
            <span class="stat-change positive">
              ▲ +0% este mês
            </span>
          </div>
        </div>

        <div class="stat-card stat-receitas">
          <div class="stat-icon-wrapper">
            <div class="stat-icon" style="background: linear-gradient(135deg, #3B82F6, #2563EB);">
              <span>📈</span>
            </div>
          </div>
          <div class="stat-content">
            <span class="stat-label">Receitas</span>
            <span class="stat-value positive">R$ {{ formatarValor(receitas) }}</span>
            <span class="stat-change positive">▲ +0% este mês</span>
          </div>
        </div>

        <div class="stat-card stat-despesas">
          <div class="stat-icon-wrapper">
            <div class="stat-icon" style="background: linear-gradient(135deg, #EF4444, #DC2626);">
              <span>📉</span>
            </div>
          </div>
          <div class="stat-content">
            <span class="stat-label">Despesas</span>
            <span class="stat-value negative">R$ {{ formatarValor(despesas) }}</span>
            <span class="stat-change negative">▲ +0% este mês</span>
          </div>
        </div>

        <div class="stat-card stat-transacoes">
          <div class="stat-icon-wrapper">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F59E0B, #D97706);">
              <span>📊</span>
            </div>
          </div>
          <div class="stat-content">
            <span class="stat-label">Transações</span>
            <span class="stat-value">{{ transacoesStore.transacoes.length }}</span>
            <span class="stat-change">Total de lançamentos</span>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="charts-grid">
        <div class="card chart-card">
          <h3>📈 Receita x Despesa</h3>
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div class="bar-container">
                <div class="bar-label">Jul</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 45%"></div>
                  <div class="bar bar-despesa" style="height: 25%"></div>
                </div>
              </div>
              <div class="bar-container">
                <div class="bar-label">Ago</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 55%"></div>
                  <div class="bar bar-despesa" style="height: 30%"></div>
                </div>
              </div>
              <div class="bar-container">
                <div class="bar-label">Set</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 35%"></div>
                  <div class="bar bar-despesa" style="height: 40%"></div>
                </div>
              </div>
              <div class="bar-container">
                <div class="bar-label">Out</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 65%"></div>
                  <div class="bar bar-despesa" style="height: 35%"></div>
                </div>
              </div>
              <div class="bar-container">
                <div class="bar-label">Nov</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 75%"></div>
                  <div class="bar bar-despesa" style="height: 20%"></div>
                </div>
              </div>
              <div class="bar-container">
                <div class="bar-label">Dez</div>
                <div class="bar-wrapper">
                  <div class="bar bar-receita" style="height: 50%"></div>
                  <div class="bar bar-despesa" style="height: 45%"></div>
                </div>
              </div>
            </div>
            <div class="chart-legend">
              <span><span class="legend-dot" style="background: #3B82F6;"></span> Receitas</span>
              <span><span class="legend-dot" style="background: #EF4444;"></span> Despesas</span>
            </div>
          </div>
        </div>

        <div class="card chart-card">
          <h3>🍕 Despesas por Categoria</h3>
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
            <span class="empty-icon">📈</span>
            <p>Nenhum dado ainda</p>
            <span class="empty-sub">Cadastre uma transação para visualizar os gráficos</span>
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
            <h3>🕒 Últimas Transações</h3>
            <router-link to="/transacoes" class="btn btn-secondary btn-sm">
              Ver todas
            </router-link>
          </div>
          
          <div v-if="transacoesStore.transacoes.length > 0" class="transaction-list">
            <div v-for="transacao in transacoesStore.ultimasTransacoes" :key="transacao.id" class="transaction-item">
              <div class="transaction-info">
                <span class="transaction-desc">{{ transacao.descricao }}</span>
                <span class="transaction-category">{{ transacao.categoria }}</span>
                <span class="transaction-date">{{ formatarData(transacao.data) }}</span>
              </div>
              <div class="transaction-value" :class="transacao.tipo === 'RECEITA' ? 'positive' : 'negative'">
                <span class="badge" :class="transacao.tipo === 'RECEITA' ? 'badge-success' : 'badge-danger'">
                  {{ transacao.tipo }}
                </span>
                {{ transacao.tipo === 'RECEITA' ? '+' : '-' }} R$ {{ formatarValor(transacao.valor) }}
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <span class="empty-icon">📭</span>
            <p>Nenhuma transação cadastrada</p>
            <router-link to="/transacoes" class="btn btn-primary btn-sm">
              <i class="pi pi-plus"></i> Adicionar
            </router-link>
          </div>
        </div>

        <div class="card">
          <h3>🎯 Meta do Mês</h3>
          <div class="meta-card">
            <div class="meta-info">
              <span class="meta-title">Economia</span>
              <span class="meta-value">R$ 500 / R$ 1.000</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" style="width: 50%"></div>
            </div>
            <span class="progress-text">50% concluído</span>
          </div>
          <div class="meta-card">
            <div class="meta-info">
              <span class="meta-title">Lazer</span>
              <span class="meta-value">R$ 200 / R$ 400</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" style="width: 50%; background: #F59E0B;"></div>
            </div>
            <span class="progress-text">50% concluído</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
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
    'Alimentação': '#22C55E',
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
  background: linear-gradient(180deg, #F8FAFC, #F1F5F9);
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
  margin-bottom: 30px;
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

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.08);
}

.stat-icon-wrapper {
  flex-shrink: 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #64748B;
  font-weight: 500;
  display: block;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0F172A;
  display: block;
  margin: 4px 0;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  color: #22C55E;
}

.stat-change.negative {
  color: #EF4444;
}

.positive { color: #22C55E; }
.negative { color: #EF4444; }

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 30px;
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
  margin-bottom: 16px;
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
  gap: 12px;
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
  gap: 4px;
  align-items: flex-end;
  height: 120px;
  width: 100%;
  justify-content: center;
}

.bar {
  width: 20px;
  border-radius: 4px 4px 0 0;
  transition: height 0.6s ease;
  min-height: 4px;
}

.bar-receita {
  background: #3B82F6;
}

.bar-despesa {
  background: #EF4444;
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
  background: #E2E8F0;
  border-radius: 4px;
  overflow: hidden;
}

.category-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s ease;
}

.category-value {
  font-size: 14px;
  font-weight: 500;
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
  margin-bottom: 16px;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 10px;
  transition: all 0.2s ease;
  background: #F8FAFC;
}

.transaction-item:hover {
  background: #EFF6FF;
  transform: translateX(4px);
}

.transaction-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.transaction-desc {
  font-weight: 500;
  color: #0F172A;
  font-size: 14px;
}

.transaction-category {
  font-size: 12px;
  color: #64748B;
}

.transaction-date {
  font-size: 11px;
  color: #94A3B8;
}

.transaction-value {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 15px;
}

.badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
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

/* Meta */
.meta-card {
  padding: 16px;
  background: #F8FAFC;
  border-radius: 10px;
  margin-bottom: 12px;
}

.meta-card:last-child {
  margin-bottom: 0;
}

.meta-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.meta-title {
  font-weight: 500;
  color: #0F172A;
  font-size: 14px;
}

.meta-value {
  font-size: 14px;
  color: #64748B;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #E2E8F0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3B82F6, #2563EB);
  border-radius: 4px;
  transition: width 0.8s ease;
}

.progress-text {
  font-size: 12px;
  color: #64748B;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 20px;
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

/* Buttons */
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
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.btn-secondary {
  background: #F1F5F9;
  color: #0F172A;
}

.btn-secondary:hover {
  background: #E2E8F0;
  transform: scale(1.02);
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

/* Animations */
@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card {
  animation: fadeSlideUp 0.4s ease forwards;
}

.stat-card:nth-child(2) { animation-delay: 0.1s; }
.stat-card:nth-child(3) { animation-delay: 0.2s; }
.stat-card:nth-child(4) { animation-delay: 0.3s; }

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