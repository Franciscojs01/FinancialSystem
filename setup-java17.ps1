# Script para instalar e configurar JDK 17 para o projeto
Write-Host "=== Setup JDK 17 para financialSystem ===" -ForegroundColor Cyan

# Verifica se JDK 17 já está instalado
$jdk17Paths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17*",
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\OpenJDK\jdk-17*"
)

$jdk17Found = $null
foreach ($path in $jdk17Paths) {
    $found = Get-ChildItem -Path (Split-Path $path) -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -like (Split-Path $path -Leaf) } | Select-Object -First 1
    if ($found) {
        $jdk17Found = $found.FullName
        break
    }
}

if ($jdk17Found) {
    Write-Host "✓ JDK 17 encontrado em: $jdk17Found" -ForegroundColor Green
    
    # Configurar JAVA_HOME temporariamente
    Write-Host "`nConfigurando JAVA_HOME para esta sessão..." -ForegroundColor Yellow
    $env:JAVA_HOME = $jdk17Found
    $env:PATH = "$jdk17Found\bin;$env:PATH"
    
    Write-Host "✓ JAVA_HOME configurado: $env:JAVA_HOME" -ForegroundColor Green
    
    # Verificar versão
    Write-Host "`nVersão do Java:" -ForegroundColor Cyan
    java -version
    
    Write-Host "`n=== Execute os testes agora ===" -ForegroundColor Cyan
    Write-Host ".\mvnw.cmd test -Dtest=UserControllerIT" -ForegroundColor Yellow
    
} else {
    Write-Host "✗ JDK 17 não encontrado!" -ForegroundColor Red
    Write-Host "`nBaixe e instale o JDK 17 de uma das seguintes opções:" -ForegroundColor Yellow
    Write-Host "1. Eclipse Temurin: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
    Write-Host "2. Oracle JDK 17: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html" -ForegroundColor Cyan
    Write-Host "3. OpenJDK 17: https://jdk.java.net/17/" -ForegroundColor Cyan
    
    Write-Host "`nApós instalar, execute este script novamente." -ForegroundColor Yellow
}
