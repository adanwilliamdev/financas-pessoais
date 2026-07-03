Write-Host "🚀 Iniciando Frontend..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Set-Location "C:\Projetos\financas-pessoais\frontend"

Write-Host "📦 Instalando dependências..." -ForegroundColor Yellow
npm install

Write-Host "🔄 Iniciando servidor de desenvolvimento..." -ForegroundColor Yellow
npm run dev

Write-Host ""
Write-Host "✅ Frontend rodando em: http://localhost:3000" -ForegroundColor Green
