# Script PowerShell para arrancar la infraestructura en Windows local
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "   LEVANTANDO INFRAESTRUCTURA (MYSQL + CLÚSTER RABBITMQ 2 NODOS)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Comprobar contenedores previos
$containers = @("mysql-carhub", "rabbitmq1", "rabbitmq2", "gracious_pike")
$running = docker ps -a --format "{{.Names}}"

foreach ($c in $containers) {
    if ($running -contains $c) {
        Write-Host "[INFO] Eliminando contenedor previo: $c" -ForegroundColor Yellow
        docker rm -f $c | Out-Null
    }
}

# 2. Levantar con Docker Compose
Write-Host "[INFO] Levantando servicios con Docker Compose..." -ForegroundColor Green
docker compose up -d

Write-Host "[INFO] Esperando 15 segundos a que inicialicen los contenedores..." -ForegroundColor Gray
Start-Sleep -Seconds 15

# 3. Unir rabbitmq2 a rabbitmq1
Write-Host "[INFO] Configurando clúster RabbitMQ..." -ForegroundColor Green
docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq2 rabbitmqctl reset
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbitmq2 rabbitmqctl start_app

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " ESTADO DEL CLÚSTER RABBITMQ:" -ForegroundColor Cyan
docker exec rabbitmq1 rabbitmqctl cluster_status

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " VERIFICACIÓN DE BASES DE DATOS MYSQL:" -ForegroundColor Cyan
docker exec mysql-carhub mysql -uroot -pzacky5023 -e "SHOW DATABASES;"
Write-Host "=================================================================" -ForegroundColor Green
Write-Host " LISTO: MySQL en 3306 y RabbitMQ en 5672/15672 con 2 Nodos" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
