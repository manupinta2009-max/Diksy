#!/usr/bin/env bash
# Script para iniciar Diksy de forma independiente
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

if ! command -v java &> /dev/null; then
    echo "Error: Java no está disponible en el sistema. Asegúrate de tener instalado Java 17 o superior."
    exit 1
fi

exec java -jar "$DIR/diksy.jar" "$@"

