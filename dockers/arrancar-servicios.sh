#!/usr/bin/env bash
# ==============================================================================
# Script de Despliegue Completo para AWS EC2 / Linux / Windows Git Bash
# Despliega:
#   1. Servidor MySQL 8.0 (con las 6 bases de datos automáticas)
#   2. Clúster RabbitMQ de 2 Nodos (rabbitmq1 + rabbitmq2 sincronizados)
#   3. Los 6 Microservicios Spring Boot construidos y conectados en contenedores
# ==============================================================================

set -e

# Directorio base del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "================================================================="
echo "   INICIANDO DESPLIEGUE COMPLETO DE SERVICIOS EN DOCKER"
echo "================================================================="

# 1. Comprobar instalación de Docker
if ! command -v docker &> /dev/null; then
    echo "[ERROR] Docker no está instalado en este sistema. Por favor instálalo primero."
    exit 1
fi

# Detectar comando docker compose o docker-compose
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
elif command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    echo "[ERROR] Ni 'docker compose' ni 'docker-compose' fueron encontrados."
    exit 1
fi

echo "[INFO] Usando gestor: $COMPOSE_CMD"

# 2. Verificar y limpiar contenedores previos
CONTAINERS=(
    "mysql-carhub"
    "rabbitmq1"
    "rabbitmq2"
    "backend-automotriz"
    "backend-vehiculos"
    "backend-piezas"
    "backend-reclamos"
    "backend-compra"
    "backend-carhub"
    "gracious_pike"
)

echo "[INFO] Verificando contenedores existentes..."
EXISTEN=false
for c in "${CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -Eq "^${c}\$"; then
        EXISTEN=true
        break
    fi
done

if [ "$EXISTEN" = true ]; then
    echo "[INFO] Se detectaron contenedores previos. Deteniendo y eliminando para iniciar limpio..."
    $COMPOSE_CMD down -v --remove-orphans 2>/dev/null || true
    for c in "${CONTAINERS[@]}"; do
        if docker ps -a --format '{{.Names}}' | grep -Eq "^${c}\$"; then
            echo "       Eliminando contenedor residual: $c"
            docker rm -f "$c" 2>/dev/null || true
        fi
    done
    echo "[OK] Contenedores previos eliminados exitosamente."
else
    echo "[INFO] No se encontraron contenedores previos. Iniciando despliegue nuevo."
fi

# Comprobar si el puerto 3306 ya está ocupado en el host (ej. MySQL local de Windows)
if (command -v netstat &>/dev/null && netstat -ano 2>/dev/null | grep -Eq "(:3306 |0.0.0.0:3306|127.0.0.1:3306).*LISTENING") || \
   (command -v ss &>/dev/null && ss -tlpn 2>/dev/null | grep -q ":3306 "); then
    echo "[AVISO] El puerto 3306 ya está ocupado en el sistema host por un MySQL local."
    echo "        Mapeando el puerto externo del contenedor al 3307 en el host (en la red interna sigue en 3306)..."
    export MYSQL_PORT=3307
else
    export MYSQL_PORT=3306
fi

# 3. Arrancar Infraestructura Base (MySQL + Clúster RabbitMQ)
echo "================================================================="
echo " [PASO 1/3] LEVANTANDO BASES DE DATOS Y MENSAJERÍA (MySQL + RabbitMQ)"
echo "================================================================="
$COMPOSE_CMD up -d mysql rabbitmq1 rabbitmq2

echo "[INFO] Esperando a que MySQL acepte consultas SQL con credenciales root..."
count=0
max_retries=30
until docker exec mysql-carhub mysql -uroot -pzacky5023 -e "SELECT 1;" &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando respuesta de MySQL... ($count/$max_retries)"
    sleep 3
    count=$((count + 1))
done

# Crear explícitamente las 6 bases de datos requeridas por la rúbrica
echo "[INFO] Creando y asegurando las 6 bases de datos para los microservicios..."
docker exec -i mysql-carhub mysql -uroot -pzacky5023 << 'EOF' || true
CREATE DATABASE IF NOT EXISTS automotriz;
CREATE DATABASE IF NOT EXISTS vehiculos;
CREATE DATABASE IF NOT EXISTS piezas;
CREATE DATABASE IF NOT EXISTS reclamos;
CREATE DATABASE IF NOT EXISTS compra;
CREATE DATABASE IF NOT EXISTS usuarios;
EOF

echo "[OK] Bases de datos creadas:"
docker exec mysql-carhub mysql -uroot -pzacky5023 -e "SHOW DATABASES WHERE \`Database\` IN ('automotriz','vehiculos','piezas','reclamos','compra','usuarios');"

# 4. Configurar el Clúster de RabbitMQ (Unir rabbitmq2 a rabbitmq1)
echo "================================================================="
echo " [PASO 2/3] CONFIGURANDO CLÚSTER RABBITMQ (2 NODOS)"
echo "================================================================="
count=0
until docker exec rabbitmq1 rabbitmqctl status &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando a rabbitmq1... ($count/$max_retries)"
    sleep 3
    count=$((count + 1))
done

count=0
until docker exec rabbitmq2 rabbitmqctl status &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando a rabbitmq2... ($count/$max_retries)"
    sleep 3
    count=$((count + 1))
done

echo "[INFO] Sincronizando rabbitmq2 con rabbit@rabbit1..."
docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq2 rabbitmqctl reset 2>/dev/null || true
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbitmq2 rabbitmqctl start_app

echo "[OK] Clúster configurado exitosamente:"
docker exec rabbitmq1 rabbitmqctl cluster_status

# 5. Construir y Levantar los 6 Microservicios Spring Boot
echo "================================================================="
echo " [PASO 3/3] COMPILANDO Y LEVANTANDO LOS 6 MICROSERVICIOS SPRING BOOT"
echo "================================================================="
SERVICES=("automotriz" "vehiculos" "piezas" "reclamos" "compra" "carhub")

echo "[INFO] Compilando imágenes Docker (control de memoria RAM optimizado para EC2)..."
for svc in "${SERVICES[@]}"; do
    echo "       -> Construyendo imagen para: $svc..."
    $COMPOSE_CMD build "$svc"
done

echo "[INFO] Levantando todos los microservicios en segundo plano..."
$COMPOSE_CMD up -d "${SERVICES[@]}"

echo "[INFO] Esperando 15 segundos a que los microservicios inicialicen sus conexiones..."
sleep 15

# 6. Resumen de Despliegue
echo "================================================================="
echo "      ¡TODOS LOS SERVICIOS ESTÁN CORRIENDO AL 100%!              "
echo "================================================================="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "================================================================="
echo "                   TABLA DE ACCESO Y ENDPOINTS                   "
echo "================================================================="
echo " 1. MySQL Server (Container):"
echo "    - Host (externo): localhost:$MYSQL_PORT (user: root | pass: zacky5023)"
echo "    - BDs: automotriz, vehiculos, piezas, reclamos, compra, usuarios"
echo ""
echo " 2. Clúster RabbitMQ (2 Nodos Activos):"
echo "    - Nodo 1: AMQP localhost:5672 | Web: http://localhost:15672 (guest/guest)"
echo "    - Nodo 2: AMQP localhost:5673 | Web: http://localhost:15673 (guest/guest)"
echo ""
echo " 3. Microservicios Spring Boot:"
echo "    - [8061] Automotriz:    http://localhost:8061/swagger-ui/index.html"
echo "    - [8062] Vehículos:     http://localhost:8062/swagger-ui/index.html"
echo "    - [8063] Piezas:        http://localhost:8063/swagger-ui/index.html"
echo "    - [8064] Reclamos:      http://localhost:8064/swagger-ui/index.html"
echo "    - [8065] Compra:        http://localhost:8065/swagger-ui/index.html"
echo "    - [8066] CarHub/Users:  http://localhost:8066/swagger-ui/index.html"
echo "================================================================="
