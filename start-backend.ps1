Write-Host "🚀 Iniciando Backend..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Set-Location "C:\Projetos\financas-pessoais\backend"

Write-Host "📦 Baixando dependências..." -ForegroundColor Yellow
mvn dependency:resolve

Write-Host "🔄 Compilando e executando..." -ForegroundColor Yellow
mvn spring-boot:run

Write-Host ""
Write-Host "✅ Backend rodando em: http://localhost:8080" -ForegroundColor Green
