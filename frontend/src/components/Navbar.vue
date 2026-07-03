<template>
  <nav class="navbar">
    <div class="container">
      <div class="nav-brand">
        <i class="pi pi-wallet"></i>
        <span>Finanças</span>
      </div>
      
      <div class="nav-menu" :class="{ 'nav-menu-open': menuOpen }">
        <router-link to="/dashboard" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">🏠</span> Dashboard
        </router-link>
        <router-link to="/transacoes" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">💸</span> Transações
        </router-link>
        <router-link to="/metas" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">🎯</span> Metas
        </router-link>
        <router-link to="/relatorios" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">📊</span> Relatórios
        </router-link>
        <router-link to="/configuracoes" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">⚙️</span> Configurações
        </router-link>
      </div>
      
      <div class="nav-actions">
        <span class="user-name">
          <i class="pi pi-user"></i> {{ authStore.getNome }}
        </span>
        <button @click="authStore.logout" class="btn btn-danger btn-sm">
          <i class="pi pi-sign-out"></i> Sair
        </button>
        <button @click="menuOpen = !menuOpen" class="menu-toggle">
          <i class="pi pi-bars"></i>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const menuOpen = ref(false)
</script>

<style scoped>
.navbar {
  background: white;
  padding: 12px 0;
  border-bottom: 1px solid #E5E7EB;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #2563EB;
}

.nav-brand i {
  font-size: 28px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: 12px;
  color: #64748B;
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s ease;
}

.nav-link:hover {
  background: #F1F5F9;
  color: #0F172A;
}

.nav-link.router-link-active {
  background: #EFF6FF;
  color: #2563EB;
}

.nav-link .nav-icon {
  font-size: 18px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #0F172A;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.btn-danger {
  background: #EF4444;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
}

.btn-danger:hover {
  background: #DC2626;
  transform: scale(1.02);
}

.btn-sm {
  padding: 6px 14px;
  font-size: 12px;
}

.menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 24px;
  color: #0F172A;
  cursor: pointer;
  padding: 4px;
}

@media (max-width: 768px) {
  .nav-menu {
    position: fixed;
    top: 68px;
    left: 0;
    right: 0;
    background: white;
    flex-direction: column;
    padding: 20px;
    border-bottom: 1px solid #E5E7EB;
    transform: translateY(-120%);
    transition: transform 0.3s ease;
  }
  
  .nav-menu-open {
    transform: translateY(0);
  }
  
  .nav-link {
    width: 100%;
    padding: 12px 16px;
  }
  
  .menu-toggle {
    display: block;
  }
  
  .user-name {
    display: none;
  }
}
</style>