<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from './store/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.checkAuth()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: var(--font-sans, 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif);
  color: #0F172A;
  background-color: #F8FAFC;
  background-image:
    radial-gradient(1100px circle at 100% -10%, rgba(99, 102, 241, 0.08), transparent 55%),
    radial-gradient(900px circle at -10% 15%, rgba(56, 189, 248, 0.07), transparent 45%);
  background-attachment: fixed;
  -webkit-font-smoothing: antialiased;
}

#app {
  min-height: 100vh;
}

button, input, select, textarea {
  font-family: inherit;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #F1F5F9;
}

::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94A3B8;
}
</style>
