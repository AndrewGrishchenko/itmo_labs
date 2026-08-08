#!/bin/bash
set -e

until pg_isready -h primary -U postgres; do
  echo "Waiting for primary..."
  sleep 2
done

rm -rf $PGDATA/*

pg_basebackup -h primary \
  -D $PGDATA \
  -U repl \
  -Fp -Xs -P -R

echo "Starting replica..."

chown -R postgres:postgres "$PGDATA"

exec su postgres -c "postgres -c config_file=/etc/postgresql/postgresql.conf -D $PGDATA -c hba_file=/etc/postgresql/pg_hba.conf"