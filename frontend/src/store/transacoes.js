import { defineStore } from 'pinia'
import api from '../utils/api'

export const useTransacoesStore = defineStore('transacoes', {
  state: () => ({
    transacoes: [],
    loading: false,
    saldo: {
      total: 0,
      mes: 0,
      categorias: {}
    }
  }),
  
  getters: {
    totalReceitas: (state) => {
      return state.transacoes
        .filter(t => t.tipo === 'RECEITA')
        .reduce((sum, t) => sum + t.valor, 0)
    },
    totalDespesas: (state) => {
      return state.transacoes
        .filter(t => t.tipo === 'DESPESA')
        .reduce((sum, t) => sum + t.valor, 0)
    },
    ultimasTransacoes: (state) => {
      return state.transacoes.slice(0, 5)
    }
  },
  
  actions: {
    async carregarTransacoes() {
      this.loading = true
      try {
        const response = await api.get('/transacoes')
        this.transacoes = response.data
        return response.data
      } catch (error) {
        console.error('Erro ao carregar transações:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async carregarSaldo() {
      try {
        const response = await api.get('/transacoes/saldo')
        this.saldo = response.data
        return response.data
      } catch (error) {
        console.error('Erro ao carregar saldo:', error)
        throw error
      }
    },
    
    async criarTransacao(transacao) {
      try {
        const response = await api.post('/transacoes', transacao)
        await this.carregarTransacoes()
        await this.carregarSaldo()
        return response.data
      } catch (error) {
        console.error('Erro ao criar transação:', error)
        throw error
      }
    },
    
    async importarArquivo(arquivo) {
      const formData = new FormData()
      formData.append('arquivo', arquivo)
      
      try {
        const response = await api.post('/transacoes/importar', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        await this.carregarTransacoes()
        await this.carregarSaldo()
        return response.data
      } catch (error) {
        console.error('Erro ao importar arquivo:', error)
        throw error
      }
    }
  }
})
