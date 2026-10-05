#!/usr/bin/env bash
# ------------------------------------------------------------------
# Solo para esta maquina: levanta una instancia de MySQL/MariaDB
# "portatil" en el directorio del usuario (no requiere permisos root).
# Si tu equipo ya tiene el servicio MySQL instalado, no uses este script.
# ------------------------------------------------------------------
set -e

DATADIR="$HOME/.local/share/hospital-mysql"
SOCKDIR="$HOME/.local/run/hospital-mysql"

mkdir -p "$DATADIR" "$SOCKDIR"

if [ ! -d "$DATADIR/mysql" ]; then
  mariadb-install-db --datadir="$DATADIR" --auth-root-authentication-method=normal \
      --skip-test-db --basedir=/usr
fi

if ! mariadb-admin --socket="$SOCKDIR/mysql.sock" -u root ping >/dev/null 2>&1; then
  nohup mariadbd --datadir="$DATADIR" \
      --socket="$SOCKDIR/mysql.sock" --port=3306 --bind-address=127.0.0.1 \
      --pid-file="$SOCKDIR/mysql.pid" --log-error="$SOCKDIR/error.log" \
      >/dev/null 2>&1 &
  sleep 5
fi

mariadb-admin --socket="$SOCKDIR/mysql.sock" -u root ping

# Crear la base (si no existe) y cargar los datos de prueba
mariadb --socket="$SOCKDIR/mysql.sock" -u root < "$(dirname "$0")/src/main/sql/creacion_base.sql"
mariadb --socket="$SOCKDIR/mysql.sock" -u root < "$(dirname "$0")/src/main/sql/datos_prueba.sql"
echo "Base 'hospital' lista para usar."
