#!/usr/bin/env bash
# ==============================================================================
# Script de Infraestructura para Administración y Auditoría de Apache Kafka
# Permite crear, listar, inspeccionar y modificar tópicos y grupos de consumidores
# directamente en los brokers del clúster CarHub.
# ==============================================================================

set -e

CONTAINER="kafka1-carhub"
BOOTSTRAP="localhost:9092"

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

mostrar_ayuda() {
    echo -e "${CYAN}=================================================================${NC}"
    echo -e "${CYAN}   CARHUB - HERRAMIENTA CLI DE ADMINISTRACIÓN KAFKA            ${NC}"
    echo -e "${CYAN}=================================================================${NC}"
    echo "Uso: ./kafka-admin.sh [comando] [opciones]"
    echo ""
    echo "Comandos disponibles:"
    echo "  listar-topicos                        Lista todos los tópicos del clúster"
    echo "  describir-topico <nombre>             Muestra detalle, réplicas, particiones y configs"
    echo "  crear-topico <nombre> [p] [r] [ret]   Crea un tópico (particiones, réplicas, retención en ms)"
    echo "  eliminar-topico <nombre>              Elimina un tópico del clúster"
    echo "  listar-grupos                         Lista los Consumer Groups registrados"
    echo "  describir-grupo <nombre_grupo>        Muestra lag, offsets y miembros del grupo"
    echo "  consumir <nombre_topico>              Lee mensajes en tiempo real desde el inicio"
    echo "  estado-cluster                        Verifica brokers activos y salud de Zookeeper"
    echo "  ayuda                                 Muestra este menú"
    echo -e "${CYAN}=================================================================${NC}"
}

comprobar_contenedor() {
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}\$"; then
        echo -e "${RED}[ERROR] El contenedor '${CONTAINER}' no está corriendo.${NC}"
        echo "Asegúrate de haber iniciado la infraestructura con ./arrancar-servicios.sh"
        exit 1
    fi
}

COMANDO="${1:-ayuda}"

case "$COMANDO" in
    listar-topicos)
        comprobar_contenedor
        echo -e "${GREEN}[INFO] Listando tópicos en el clúster Kafka...${NC}"
        docker exec -t "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --list
        ;;

    describir-topico)
        comprobar_contenedor
        TOPIC="$2"
        if [ -z "$TOPIC" ]; then
            echo -e "${RED}[ERROR] Debes especificar el nombre del tópico.${NC}"
            echo "Ejemplo: ./kafka-admin.sh describir-topico carhub.ventas.streaming"
            exit 1
        fi
        echo -e "${GREEN}[INFO] Detalle del tópico '${TOPIC}':${NC}"
        docker exec -t "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --describe --topic "$TOPIC"
        ;;

    crear-topico)
        comprobar_contenedor
        TOPIC="$2"
        PARTITIONS="${3:-3}"
        REPLICAS="${4:-2}"
        RETENTION="${5:-604800000}"

        if [ -z "$TOPIC" ]; then
            echo -e "${RED}[ERROR] Debes indicar el nombre del tópico.${NC}"
            echo "Ejemplo: ./kafka-admin.sh crear-topico mi.topico 3 2 604800000"
            exit 1
        fi

        echo -e "${GREEN}[INFO] Creando tópico '${TOPIC}' con ${PARTITIONS} particiones, factor ${REPLICAS} y retención ${RETENTION}ms...${NC}"
        docker exec -t "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --create \
            --topic "$TOPIC" \
            --partitions "$PARTITIONS" \
            --replication-factor "$REPLICAS" \
            --config retention.ms="$RETENTION" \
            --config cleanup.policy=delete
        echo -e "${GREEN}[OK] Tópico creado exitosamente.${NC}"
        ;;

    eliminar-topico)
        comprobar_contenedor
        TOPIC="$2"
        if [ -z "$TOPIC" ]; then
            echo -e "${RED}[ERROR] Debes indicar el nombre del tópico a eliminar.${NC}"
            exit 1
        fi
        echo -e "${YELLOW}[AVISO] Eliminando tópico '${TOPIC}'...${NC}"
        docker exec -t "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --delete --topic "$TOPIC"
        echo -e "${GREEN}[OK] Tópico eliminado.${NC}"
        ;;

    listar-grupos)
        comprobar_contenedor
        echo -e "${GREEN}[INFO] Grupos de consumidores activos en el clúster:${NC}"
        docker exec -t "$CONTAINER" kafka-consumer-groups --bootstrap-server "$BOOTSTRAP" --list
        ;;

    describir-grupo)
        comprobar_contenedor
        GRUPO="$2"
        if [ -z "$GRUPO" ]; then
            echo -e "${RED}[ERROR] Debes indicar el nombre del grupo de consumidores.${NC}"
            echo "Ejemplo: ./kafka-admin.sh describir-grupo carhub-analytics-ventas-group"
            exit 1
        fi
        echo -e "${GREEN}[INFO] Inspeccionando offsets y lag para el grupo '${GRUPO}':${NC}"
        docker exec -t "$CONTAINER" kafka-consumer-groups --bootstrap-server "$BOOTSTRAP" --describe --group "$GRUPO"
        ;;

    consumir)
        comprobar_contenedor
        TOPIC="$2"
        if [ -z "$TOPIC" ]; then
            echo -e "${RED}[ERROR] Debes indicar el tópico a consumir.${NC}"
            exit 1
        fi
        echo -e "${GREEN}[INFO] Escuchando mensajes de '${TOPIC}' (Ctrl+C para salir)...${NC}"
        docker exec -it "$CONTAINER" kafka-console-consumer --bootstrap-server "$BOOTSTRAP" \
            --topic "$TOPIC" --from-beginning --property print.key=true --property print.timestamp=true
        ;;

    estado-cluster)
        comprobar_contenedor
        echo -e "${CYAN}=== ESTADO DE CONTENEDORES KAFKA & ZOOKEEPER ===${NC}"
        docker ps --filter "name=zookeeper" --filter "name=kafka" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        echo ""
        echo -e "${CYAN}=== TOPOLOGÍA DE ZOOKEEPER (BROKERS REGISTRADOS) ===${NC}"
        docker exec -t zookeeper-carhub zookeeper-shell localhost:2181 ls /brokers/ids 2>/dev/null || true
        ;;

    ayuda|*)
        mostrar_ayuda
        ;;
esac
