<div align="center">

# 💰 Finanças Pessoais

### Sistema completo de gestão financeira desenvolvido com Spring Boot + Vue.js

Controle receitas, despesas, metas financeiras e importe extratos bancários (CSV/OFX) através de uma interface moderna e responsiva.

---

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-green?style=for-the-badge&logo=springboot)
![Vue](https://img.shields.io/badge/Vue.js-3-42b883?style=for-the-badge&logo=vuedotjs)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens)
![License](https://img.shields.io/badge/License-MIT-success?style=for-the-badge)

</div>

---

# 📖 Sobre o Projeto

O **Finanças Pessoais** é uma aplicação Full Stack desenvolvida para auxiliar usuários no gerenciamento de suas finanças.

O sistema permite controlar receitas, despesas, metas de economia e importar extratos bancários, fornecendo uma visão clara da saúde financeira através de dashboards e gráficos interativos.

Este projeto foi desenvolvido para demonstrar conhecimentos em:

- Arquitetura REST
- Spring Security + JWT
- Vue.js
- PostgreSQL
- Docker
- Boas práticas de desenvolvimento
- Design moderno inspirado em produtos como **Linear**, **Vercel** e **Stripe**

---

# ✨ Funcionalidades

## 🔐 Autenticação

- Login com JWT
- Cadastro de usuários
- Criptografia de senha
- Proteção de rotas
- Controle de sessão

---

## 💳 Gestão Financeira

- Cadastro de receitas
- Cadastro de despesas
- CRUD completo
- Categorias personalizadas
- Histórico de transações
- Precisão monetária utilizando BigDecimal

---

## 📥 Importação Bancária

- Importação de arquivos CSV
- Importação de arquivos OFX
- Processamento automático
- Validação dos dados

---

## 📊 Dashboard

- Saldo Total
- Receita Mensal
- Despesa Mensal
- Últimas transações
- Despesas por categoria
- Receita x Despesa
- Indicadores financeiros

---

## 🎯 Metas Financeiras

- Criar metas
- Editar metas
- Acompanhar progresso
- Percentual concluído

---

## 📈 Relatórios

- Evolução mensal
- Gastos por categoria
- Receitas por período
- Ranking de categorias

---

## ⚙️ Configurações

- Alteração de senha
- Perfil do usuário
- Preferências
- Exportação de dados

---

# 🏗 Arquitetura

```text
               Vue.js + Vite
                      │
                      │ HTTP (Axios)
                      ▼
             Spring Boot REST API
                      │
          Spring Security + JWT
                      │
               Spring Data JPA
                      │
                 PostgreSQL
```

---

# 🛠 Tecnologias

## Backend

| Tecnologia | Descrição |
|------------|-----------|
| Java 17 | Linguagem principal |
| Spring Boot | Framework |
| Spring Security | Autenticação |
| JWT | Tokens |
| Spring Data JPA | Persistência |
| PostgreSQL | Banco de dados |
| Maven | Gerenciamento de dependências |

## Frontend

| Tecnologia | Descrição |
|------------|-----------|
| Vue 3 | Framework |
| Vite | Build Tool |
| Pinia | Gerenciamento de Estado |
| Vue Router | Rotas |
| Axios | Comunicação HTTP |
| PrimeVue | Componentes |
| Chart.js | Gráficos |
| SweetAlert2 | Alertas |

## Infraestrutura

- Docker
- Docker Compose
- Git

---

# 🚀 Executando o Projeto

## Pré-requisitos

- Java 17+
- Maven
- Docker
- Node.js 18+

---

## 1️⃣ Clonar o projeto

```bash
git clone https://github.com/adanwilliamdev/financas-pessoais.git

cd financas-pessoais
```

---

## 2️⃣ Banco de Dados

```bash
docker-compose up -d
```

---

## 3️⃣ Backend

```bash
cd backend

mvn clean install

mvn spring-boot:run
```

Servidor:

```text
http://localhost:8080
```

---

## 4️⃣ Frontend

```bash
cd frontend

npm install

npm run dev
```

Servidor:

```text
http://localhost:3001
```

---

# 🔑 Usuários de Teste

| Usuário | Senha |
|---------|--------|
| admin@financas.com | admin123 |
| usuario@financas.com | usuario123 |

---

# 📂 Estrutura do Projeto

```text
financas-pessoais
│
├── backend
│   ├── config
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   ├── security
│   ├── service
│   └── resources
│
├── frontend
│   ├── components
│   ├── router
│   ├── store
│   ├── utils
│   └── views
│
├── docker-compose.yml
└── README.md
```

---

# 📡 API

## Autenticação

| Método | Endpoint |
|---------|----------|
| POST | `/api/auth/login` |
| POST | `/api/auth/register` |

## Transações

| Método | Endpoint |
|---------|----------|
| GET | `/api/transacoes` |
| POST | `/api/transacoes` |
| PUT | `/api/transacoes/{id}` |
| DELETE | `/api/transacoes/{id}` |
| GET | `/api/transacoes/periodo` |
| POST | `/api/transacoes/importar` |

## Metas

| Método | Endpoint |
|---------|----------|
| GET | `/api/metas` |
| POST | `/api/metas` |
| PUT | `/api/metas/{id}` |
| DELETE | `/api/metas/{id}` |

---

# 🧪 Testes

## Backend

```bash
mvn test
```

## Frontend

```bash
npm run build
```

---

# 🚀 Deploy

Em breve o projeto estará disponível online.

- Frontend: **Vercel**
- Backend: **Render**
- Banco de Dados: **PostgreSQL**

---

# 🤝 Contribuição

Contribuições são sempre bem-vindas!

```bash
# Fork

# Create Branch

# Commit

# Push

# Open Pull Request
```

---

# 👨‍💻 Autor

**Adan William Oliveira Santos**

### GitHub

https://github.com/adanwilliamdev

### LinkedIn

https://www.linkedin.com/in/adanwilliamdev/

---

# ⭐ Gostou do projeto?

Se este projeto foi útil para você, deixe uma ⭐ no repositório.

Isso ajuda bastante na divulgação e incentiva o desenvolvimento de novas funcionalidades.

---

<div align="center">

Desenvolvido com ❤️ utilizando Java, Spring Boot e Vue.js.

</div>
