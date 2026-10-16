#!/usr/bin/env bash
# Wrapper para arrancar la infraestructura desde la raíz del proyecto
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash "$DIR/dockers/arrancar-servicios.sh"
