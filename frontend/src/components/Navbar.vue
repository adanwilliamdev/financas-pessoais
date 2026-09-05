<template>
  <nav class="navbar">
    <div class="container">
      <div class="nav-brand">
        <span class="nav-brand-badge"><i class="pi pi-wallet"></i></span>
        <span>Finanças</span>
      </div>

      <div class="nav-menu" :class="{ 'nav-menu-open': menuOpen }">
        <router-link to="/dashboard" class="nav-link" @click="menuOpen = false">
          <i class="pi pi-home nav-icon"></i> Dashboard
        </router-link>
        <router-link to="/transacoes" class="nav-link" @click="menuOpen = false">
          <i class="pi pi-arrow-right-arrow-left nav-icon"></i> Transações
        </router-link>
        <router-link to="/metas" class="nav-link" @click="menuOpen = false">
          <i class="pi pi-flag nav-icon"></i> Metas
        </router-link>
        <router-link to="/relatorios" class="nav-link" @click="menuOpen = false">
          <i class="pi pi-chart-bar nav-icon"></i> Relatórios
        </router-link>
        <router-link to="/configuracoes" class="nav-link" @click="menuOpen = false">
          <i class="pi pi-cog nav-icon"></i> Configurações
        </router-link>
      </div>

      <div class="nav-actions">
        <div class="user-menu" ref="userMenuRef">
          <button class="user-trigger" @click="userMenuOpen = !userMenuOpen">
            <span class="avatar">{{ initials }}</span>
            <span class="user-name">{{ authStore.getNome }}</span>
            <i class="pi pi-chevron-down user-chevron"></i>
          </button>

          <div v-if="userMenuOpen" class="user-dropdown">
            <router-link to="/configuracoes" class="dropdown-item" @click="userMenuOpen = false">
              <i class="pi pi-user"></i> Meu Perfil
            </router-link>
            <router-link to="/configuracoes" class="dropdown-item" @click="userMenuOpen = false">
              <i class="pi pi-cog"></i> Configurações
            </router-link>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item dropdown-item-danger" @click="authStore.logout">
              <i class="pi pi-sign-out"></i> Sair
            </button>
          </div>
        </div>

        <button @click="menuOpen = !menuOpen" class="menu-toggle">
          <i class="pi pi-bars"></i>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const menuOpen = ref(false)
const userMenuOpen = ref(false)
const userMenuRef = ref(null)

const initials = computed(() => {
  const nome = authStore.getNome || ''
  return nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('') || 'U'
})

function handleClickOutside(e) {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target)) {
    userMenuOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.navbar {
  padding: 12px 0;
  border-bottom: 1px solid #E2E8F0;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.72);
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
  font-size: 19px;
  font-weight: 800;
  color: #1E1B4B;
  letter-spacing: -0.02em;
}

.nav-brand-badge {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6366F1 0%, #4338CA 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.35);
  font-size: 16px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 10px;
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
  background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%);
  color: #4338CA;
  font-weight: 600;
  box-shadow: inset 0 0 0 1px rgba(79, 70, 229, 0.12);
}

.nav-link .nav-icon {
  font-size: 16px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Avatar / user dropdown */
.user-menu {
  position: relative;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px 8px 4px 4px;
  border-radius: 999px;
  transition: background 0.2s ease;
}

.user-trigger:hover {
  background: #F1F5F9;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #818CF8 0%, #4F46E5 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.user-name {
  font-size: 14px;
  color: #0F172A;
  font-weight: 500;
}

.user-chevron {
  font-size: 11px;
  color: #94A3B8;
}

.user-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  width: 200px;
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border-radius: 8px;
  color: #334155;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  background: none;
  border: none;
  cursor: pointer;
  width: 100%;
  text-align: left;
  transition: background 0.15s ease;
}

.dropdown-item:hover {
  background: #F1F5F9;
}

.dropdown-item-danger {
  color: #EF4444;
}

.dropdown-item-danger:hover {
  background: #FEF2F2;
}

.dropdown-divider {
  height: 1px;
  background: #E2E8F0;
  margin: 4px 2px;
}

.menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 22px;
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
    border-bottom: 1px solid #E2E8F0;
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
