Write-Host "🚀 Iniciando Finanças Pessoais..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 Passos para iniciar o projeto:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Inicie o banco PostgreSQL:"
Write-Host "   docker-compose up -d"
Write-Host ""
Write-Host "2. Em um terminal PowerShell, inicie o backend:"
Write-Host "   .\start-backend.ps1"
Write-Host ""
Write-Host "3. Em OUTRO terminal PowerShell, inicie o frontend:"
Write-Host "   .\start-frontend.ps1"
Write-Host ""
Write-Host "4. Acesse: http://localhost:3000"
Write-Host ""
Write-Host "🔑 Credenciais:"
Write-Host "   admin@financas.com / admin123"
Write-Host "   usuario@financas.com / usuario123"
Write-Host ""
Write-Host "📝 Comandos úteis:"
Write-Host "   docker-compose logs -f  # Ver logs do banco"
Write-Host "   docker-compose down     # Parar banco"
Write-Host ""
Write-Host "✅ Projeto pronto para uso!" -ForegroundColor Green
