# Script PowerShell para arrancar todo el sistema (MySQL + RabbitMQ 2 Nodos + 6 Microservicios)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "   LEVANTANDO TODO EL SISTEMA EN DOCKER (INFRA + MICROSERVICIOS)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Comprobar contenedores previos
$containers = @(
    "mysql-carhub",
    "rabbitmq1",
    "rabbitmq2",
    "backend-automotriz",
    "backend-vehiculos",
    "backend-piezas",
    "backend-reclamos",
    "backend-compra",
    "backend-carhub",
    "gracious_pike"
)
$running = docker ps -a --format "{{.Names}}"

foreach ($c in $containers) {
    if ($running -contains $c) {
        Write-Host "[INFO] Eliminando contenedor previo: $c" -ForegroundColor Yellow
        docker rm -f $c | Out-Null
    }
}

# Comprobar si el puerto 3306 ya está ocupado en Windows
$portInUse = Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-Host "[AVISO] Puerto 3306 ocupado por un servicio local (MySQL Windows). Mapeando MySQL contenedor a 3307..." -ForegroundColor Yellow
    $env:MYSQL_PORT = "3307"
} else {
    $env:MYSQL_PORT = "3306"
}

# 2. Levantar MySQL y RabbitMQ primero
Write-Host "[INFO] Levantando MySQL y RabbitMQ..." -ForegroundColor Green
docker compose up -d mysql rabbitmq1 rabbitmq2

Write-Host "[INFO] Esperando a que MySQL responda..." -ForegroundColor Gray
$count = 0
do {
    Start-Sleep -Seconds 3
    $count++
    $check = docker exec mysql-carhub mysqladmin ping -h localhost -uroot -pzacky5023 --silent 2>$null
} while ($LASTEXITCODE -ne 0 -and $count -lt 20)

# 3. Crear las 6 bases de datos
Write-Host "[INFO] Verificando y creando las 6 bases de datos..." -ForegroundColor Green
docker exec mysql-carhub mysql -uroot -pzacky5023 -e "CREATE DATABASE IF NOT EXISTS automotriz; CREATE DATABASE IF NOT EXISTS vehiculos; CREATE DATABASE IF NOT EXISTS piezas; CREATE DATABASE IF NOT EXISTS reclamos; CREATE DATABASE IF NOT EXISTS compra; CREATE DATABASE IF NOT EXISTS usuarios;"

# 4. Unir rabbitmq2 a rabbitmq1
Write-Host "[INFO] Configurando clúster RabbitMQ..." -ForegroundColor Green
docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq2 rabbitmqctl reset
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbitmq2 rabbitmqctl start_app

# 5. Compilar y levantar los 6 microservicios
Write-Host "[INFO] Compilando y levantando los 6 microservicios Spring Boot..." -ForegroundColor Green
docker compose up -d --build automotriz vehiculos piezas reclamos compra carhub

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " ESTADO ACTUAL DE CONTENEDORES:" -ForegroundColor Cyan
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host "=================================================================" -ForegroundColor Green
Write-Host " SERVICIOS LISTOS:" -ForegroundColor Green
Write-Host " - MySQL (Host: $env:MYSQL_PORT | Red interna: 3306) con 6 BDs creadas" -ForegroundColor Green
Write-Host " - RabbitMQ Clúster (5672 / 15672)" -ForegroundColor Green
Write-Host " - Automotriz:    http://localhost:8061/swagger-ui/index.html" -ForegroundColor Green
Write-Host " - Vehículos:     http://localhost:8062/swagger-ui/index.html" -ForegroundColor Green
Write-Host " - Piezas:        http://localhost:8063/swagger-ui/index.html" -ForegroundColor Green
Write-Host " - Reclamos:      http://localhost:8064/swagger-ui/index.html" -ForegroundColor Green
Write-Host " - Compra:        http://localhost:8065/swagger-ui/index.html" -ForegroundColor Green
Write-Host " - CarHub/Users:  http://localhost:8066/swagger-ui/index.html" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
