import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Interceptor para adicionar token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    console.log('Token encontrado:', token ? 'Sim' : 'Não')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('Authorization header adicionado')
    } else {
      console.warn('Token não encontrado no localStorage')
    }
    console.log('Request:', config.method.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Interceptor para tratar respostas
api.interceptors.response.use(
  (response) => {
    console.log('Response:', response.status, response.config.url)
    return response
  },
  (error) => {
    console.error('Response error:', error.response?.status, error.response?.data)
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.log('Token inválido ou expirado, redirecionando para login')
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      delete api.defaults.headers.common['Authorization']
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api