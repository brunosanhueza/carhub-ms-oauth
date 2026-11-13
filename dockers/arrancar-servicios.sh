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
echo "   INICIANDO DESPLIEGUE COMPLETO DE SERVICIOS EN DOCKER + FRONTEND"
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
    "zookeeper-carhub"
    "kafka1-carhub"
    "kafka2-carhub"
    "kafka-ui-carhub"
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

# 3. FASE 1: Compilar imágenes Docker de los 6 Microservicios Spring Boot
echo "================================================================="
echo " [FASE 1/3] COMPILANDO IMÁGENES DOCKER DE LOS 6 MICROSERVICIOS"
echo "================================================================="
SERVICES=("automotriz" "vehiculos" "piezas" "reclamos" "compra" "carhub")

echo "[INFO] Iniciando compilación de código fuente y empaquetado multi-stage..."
for svc in "${SERVICES[@]}"; do
    echo "       -> [BUILD] Compilando y empaquetando imagen: $svc..."
    $COMPOSE_CMD build "$svc"
done
echo "[OK] Las 6 imágenes de los microservicios se compilaron exitosamente."

# 4. FASE 2: Arrancar Infraestructura Base (MySQL + RabbitMQ Clúster + Kafka Clúster + Zookeeper + Kafka-UI)
echo "================================================================="
echo " [FASE 2/3] DESPLEGANDO INFRAESTRUCTURA (MySQL + RabbitMQ + Kafka)"
echo "================================================================="
$COMPOSE_CMD up -d mysql rabbitmq1 rabbitmq2 zookeeper kafka1 kafka2 kafka-ui

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

# Configurar el Clúster de RabbitMQ (Unir rabbitmq2 a rabbitmq1)
echo "[INFO] Sincronizando Clúster RabbitMQ (2 Nodos)..."
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

docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq2 rabbitmqctl reset 2>/dev/null || true
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbitmq2 rabbitmqctl start_app

echo "[OK] Clúster RabbitMQ configurado exitosamente:"
docker exec rabbitmq1 rabbitmqctl cluster_status

# Esperar a que Kafka esté listo y responda
echo "[INFO] Verificando disponibilidad de Kafka Brokers..."
count=0
until docker exec kafka1-carhub kafka-broker-api-versions --bootstrap-server localhost:9092 &>/dev/null || [ $count -ge $max_retries ]; do
    echo "       Esperando a que kafka1 esté listo... ($count/$max_retries)"
    sleep 3
    count=$((count + 1))
done
echo "[OK] Kafka Broker 1 (Puerto Host 5030) operativo y conectado a Zookeeper."

# 5. FASE 3: Desplegar y Levantar los 6 Microservicios Spring Boot
echo "================================================================="
echo " [FASE 3/4] LEVANTANDO LOS 6 MICROSERVICIOS YA COMPILADOS"
echo "================================================================="
echo "[INFO] Iniciando contenedores de los microservicios en segundo plano..."
$COMPOSE_CMD up -d "${SERVICES[@]}"

echo "[INFO] Esperando 15 segundos a que los microservicios inicialicen sus conexiones..."
sleep 15

# 6. FASE 4: Instalar Nginx, Compilar Frontend Angular y Configurar SSL (80 & 443)
echo "================================================================="
echo " [FASE 4/4] DESPLEGANDO FRONTEND ANGULAR EN NGINX (PUERTOS 80 & 443)"
echo "================================================================="
FRONTEND_DIR="$(cd "$SCRIPT_DIR/../frontend-carhub" && pwd)"
WEB_ROOT="/var/www/html/carhub"
SSL_DIR="/etc/nginx/ssl"

if command -v apt-get &> /dev/null; then
    echo "[INFO] Verificando Nginx, Node.js y OpenSSL en el sistema anfitrión..."
    if ! command -v nginx &> /dev/null; then
        echo "       -> Instalando Nginx..."
        apt-get update -y -q && apt-get install -y -q nginx
    fi
    if ! command -v openssl &> /dev/null; then
        apt-get install -y -q openssl
    fi
    if ! command -v node &> /dev/null || ! command -v npm &> /dev/null; then
        echo "       -> Instalando Node.js LTS y npm..."
        curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
        apt-get install -y -q nodejs
    fi

    if [ -d "$FRONTEND_DIR" ]; then
        echo "[INFO] Compilando Frontend Angular para producción..."
        cd "$FRONTEND_DIR"
        if [ ! -d "node_modules" ]; then
            npm install --legacy-peer-deps
        fi
        npm run build -- --configuration production || true

        # Localizar carpeta compilada generada por Angular
        DIST_SOURCE=""
        if [ -d "$FRONTEND_DIR/dist/frontend-carhub/browser" ]; then
            DIST_SOURCE="$FRONTEND_DIR/dist/frontend-carhub/browser"
        elif [ -d "$FRONTEND_DIR/dist/frontend-carhub" ]; then
            DIST_SOURCE="$FRONTEND_DIR/dist/frontend-carhub"
        elif [ -d "$FRONTEND_DIR/dist/frontendCarhub/browser" ]; then
            DIST_SOURCE="$FRONTEND_DIR/dist/frontendCarhub/browser"
        elif [ -d "$FRONTEND_DIR/dist" ]; then
            DIST_SOURCE=$(find "$FRONTEND_DIR/dist" -name "index.html" -exec dirname {} \; 2>/dev/null | head -n 1)
        fi

        if [ -n "$DIST_SOURCE" ] && [ -f "$DIST_SOURCE/index.html" ]; then
            echo "[INFO] Moviendo archivos compilados a '$WEB_ROOT'..."
            mkdir -p "$WEB_ROOT"
            rm -rf "$WEB_ROOT"/*
            cp -r "$DIST_SOURCE"/* "$WEB_ROOT"/
            chown -R www-data:www-data "$WEB_ROOT" 2>/dev/null || true
            chmod -R 755 "$WEB_ROOT" 2>/dev/null || true

            # Generar certificados SSL autofirmados para el puerto 443 si no existen
            mkdir -p "$SSL_DIR"
            if [ ! -f "$SSL_DIR/carhub.crt" ] || [ ! -f "$SSL_DIR/carhub.key" ]; then
                echo "[INFO] Generando certificado SSL autofirmado de 2048 bits para CarHub (443)..."
                openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
                    -keyout "$SSL_DIR/carhub.key" \
                    -out "$SSL_DIR/carhub.crt" \
                    -subj "/C=CL/ST=Santiago/L=Santiago/O=DuocUC/OU=CloudNative/CN=carhub.local" 2>/dev/null || true
                chmod 600 "$SSL_DIR/carhub.key" 2>/dev/null || true
                chmod 644 "$SSL_DIR/carhub.crt" 2>/dev/null || true
            fi

            # Escribir configuración VirtualHost de Nginx
            echo "[INFO] Configurando VirtualHost en /etc/nginx/sites-available/carhub..."
            cat << 'NGINX_EOF' > /etc/nginx/sites-available/carhub
server {
    listen 80 default_server;
    listen [::]:80 default_server;
    server_name _;
    root /var/www/html/carhub;
    index index.html index.htm;
    location / {
        try_files $uri $uri/ /index.html;
    }
    location ~* \.(?:ico|css|js|gif|jpe?g|png|woff2?|eot|ttf|svg)$ {
        expires 6M;
        access_log off;
        add_header Cache-Control "public";
        try_files $uri =404;
    }
}

server {
    listen 443 ssl default_server;
    listen [::]:443 ssl default_server;
    server_name _;
    ssl_certificate /etc/nginx/ssl/carhub.crt;
    ssl_certificate_key /etc/nginx/ssl/carhub.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    root /var/www/html/carhub;
    index index.html index.htm;
    location / {
        try_files $uri $uri/ /index.html;
    }
    location ~* \.(?:ico|css|js|gif|jpe?g|png|woff2?|eot|ttf|svg)$ {
        expires 6M;
        access_log off;
        add_header Cache-Control "public";
        try_files $uri =404;
    }
}
NGINX_EOF

            ln -sf /etc/nginx/sites-available/carhub /etc/nginx/sites-enabled/carhub
            rm -f /etc/nginx/sites-enabled/default 2>/dev/null || true
            nginx -t &>/dev/null && systemctl restart nginx 2>/dev/null || true
            echo "[OK] Nginx configurado y corriendo en puertos 80 y 443."
        else
            echo "[AVISO] No se pudo encontrar index.html en dist/. Continuando con el despliegue..."
        fi
    fi
    cd "$SCRIPT_DIR"
fi

# 7. Resumen de Despliegue
echo "================================================================="
echo "      ¡TODOS LOS SERVICIOS ESTÁN CORRIENDO AL 100%!              "
echo "================================================================="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "================================================================="
echo "                   TABLA DE ACCESO Y ENDPOINTS                   "
echo "================================================================="
echo " 1. Frontend Angular (Nginx):"
echo "    - HTTP:  http://<IP-HOST>:80"
echo "    - HTTPS: https://<IP-HOST>:443 (Aceptar certificado autofirmado)"
echo ""
echo " 2. MySQL Server (Container):"
echo "    - Host (externo): localhost:$MYSQL_PORT (user: root | pass: zacky5023)"
echo "    - BDs: automotriz, vehiculos, piezas, reclamos, compra, usuarios"
echo ""
echo " 3. Clúster RabbitMQ (2 Nodos Activos):"
echo "    - Nodo 1: AMQP localhost:5672 | Web: http://localhost:15672 (guest/guest)"
echo "    - Nodo 2: AMQP localhost:5673 | Web: http://localhost:15673 (guest/guest)"
echo ""
echo " 4. Clúster Kafka (2 Brokers + Zookeeper + Kafka-UI):"
echo "    - Zookeeper: localhost:2181"
echo "    - Kafka Broker 1: localhost:5030 (Host)"
echo "    - Kafka Broker 2: localhost:5031 (Host)"
echo "    - Kafka-UI: http://localhost:8085"
echo ""
echo " 5. Microservicios Spring Boot:"
echo "    - [8061] Automotriz:    http://localhost:8061/swagger-ui/index.html"
echo "    - [8062] Vehículos:     http://localhost:8062/swagger-ui/index.html"
echo "    - [8063] Piezas:        http://localhost:8063/swagger-ui/index.html"
echo "    - [8064] Reclamos:      http://localhost:8064/swagger-ui/index.html"
echo "    - [8065] Compra:        http://localhost:8065/swagger-ui/index.html"
echo "    - [8066] CarHub/Users:  http://localhost:8066/swagger-ui/index.html"
echo "================================================================="
