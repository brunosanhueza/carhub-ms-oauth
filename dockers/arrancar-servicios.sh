#!/usr/bin/env bash
# ==============================================================================
# Script de Despliegue de Infraestructura para EC2 (Cloud Native - Duoc UC)
# Levanta MySQL (con 6 bases de datos) y el Clúster RabbitMQ de 2 Nodos
# ==============================================================================

set -e

# Directorio del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "================================================================="
echo "   INICIANDO DESPLIEGUE DE INFRAESTRUCTURA EN AWS EC2"
echo "================================================================="

# 1. Comprobar e instalar Docker / Docker Compose si no estuvieran presentes
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

echo "[INFO] Usando: $COMPOSE_CMD"

# 2. Verificar si existen contenedores previos y limpiarlos
CONTAINERS=("mysql-carhub" "rabbitmq1" "rabbitmq2" "gracious_pike")
EXISTEN=false

for c in "${CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -Eq "^${c}\$"; then
        EXISTEN=true
        break
    fi
done

if [ "$EXISTEN" = true ]; then
    echo "[INFO] Se detectaron contenedores previos. Procediendo a detener y eliminar..."
    $COMPOSE_CMD down -v --remove-orphans 2>/dev/null || true
    for c in "${CONTAINERS[@]}"; do
        if docker ps -a --format '{{.Names}}' | grep -Eq "^${c}\$"; then
            echo "       Eliminando contenedor residual: $c"
            docker rm -f "$c" 2>/dev/null || true
        fi
    done
    echo "[OK] Contenedores antiguos eliminados correctamente."
else
    echo "[INFO] No se encontraron contenedores existentes. Creando desde cero..."
fi

# 3. Levantar los servicios con Docker Compose
echo "[INFO] Levantando servicios con Docker Compose (MySQL + RabbitMQ 2 Nodos)..."
$COMPOSE_CMD up -d

echo "[INFO] Esperando a que los servicios inicien (15 segundos)..."
sleep 15

# 4. Configurar el Clúster de RabbitMQ (Unir rabbitmq2 a rabbitmq1)
echo "[INFO] Configurando integración del Clúster RabbitMQ entre rabbit@rabbit1 y rabbit@rabbit2..."
max_retries=10
count=0
until docker exec rabbitmq1 rabbitmqctl status &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando a que rabbitmq1 responda... ($count/$max_retries)"
    sleep 3
    ((count++))
done

count=0
until docker exec rabbitmq2 rabbitmqctl status &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando a que rabbitmq2 responda... ($count/$max_retries)"
    sleep 3
    ((count++))
done

echo "[INFO] Uniendo rabbitmq2 al clúster de rabbitmq1..."
docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq2 rabbitmqctl reset 2>/dev/null || true
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbitmq2 rabbitmqctl start_app

echo "================================================================="
echo " [CLÚSTER DE RABBITMQ - ESTADO ACTUAL]"
echo "================================================================="
docker exec rabbitmq1 rabbitmqctl cluster_status

# 5. Verificar MySQL y creación automática de las 6 bases de datos
echo "================================================================="
echo " [VERIFICACIÓN DE BASES DE DATOS MYSQL]"
echo "================================================================="
count=0
until docker exec mysql-carhub mysql -uroot -pzacky5023 -e "SHOW DATABASES;" &>/dev/null || [ $count -ge 10 ]; do
    echo "       Esperando a que MySQL acepte conexiones..."
    sleep 3
    ((count++))
done

# Ejecutar el script SQL de creación de bases de datos por si acaso el volumen ya existía
docker exec -i mysql-carhub mysql -uroot -pzacky5023 << 'EOF'
CREATE DATABASE IF NOT EXISTS automotriz;
CREATE DATABASE IF NOT EXISTS vehiculos;
CREATE DATABASE IF NOT EXISTS piezas;
CREATE DATABASE IF NOT EXISTS reclamos;
CREATE DATABASE IF NOT EXISTS compra;
CREATE DATABASE IF NOT EXISTS usuarios;
EOF

docker exec mysql-carhub mysql -uroot -pzacky5023 -e "SHOW DATABASES LIKE '%';"

echo "================================================================="
echo " INFRAESTRUCTURA DESPLEGADA Y LISTA AL 100%"
echo "================================================================="
echo " Servidor MySQL:"
echo "   - Host: localhost:3306"
echo "   - Usuario: root | Password: zacky5023"
echo "   - Bases de datos creadas: automotriz, vehiculos, piezas, reclamos, compra, usuarios"
echo ""
echo " Clúster RabbitMQ (2 Nodos Activos):"
echo "   - Nodo 1 (Broker): localhost:5672"
echo "   - Nodo 1 (Panel Web): http://localhost:15672 (guest / guest)"
echo "   - Nodo 2 (Broker): localhost:5673"
echo "   - Nodo 2 (Panel Web): http://localhost:15673 (guest / guest)"
echo "================================================================="
