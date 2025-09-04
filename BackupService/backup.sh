#!/bin/bash
set -e

echo ">> Iniciando container de backup"

# Espera MinIO prod subir
echo '>> Esperando MinIO prod...'
until curl -s http://minio:9000/minio/health/live > /dev/null; do
  echo '>> MinIO prod ainda não disponível, aguardando...'
  sleep 3
done

# Configurar aliases mc
echo '>> Configurando Aliases MC...'
mc alias set prod http://minio:9000 ${MINIO_ADMIN_USER} ${MINIO_ADMIN_PASSWORD}
echo ">> Aliases configurados:"
mc alias list

# Cria bucket de backups
mc mb --ignore-existing prod/backups

# Habilitando versionamento
readarray -t bucket_array <<< "$(mc ls prod | awk '{print $NF}' | sed 's:/$::')"
for bucket in "${bucket_array[@]}"; do
  echo "Bucket encontrado: $bucket"
  mc version enable prod/$bucket
done

# Diretórios temporários
mkdir -p /backup/postgres /backup/mongo /backup/logs

echo ">> Iniciando backup: $(date)"

# --- Limpeza de backups antigos (7 dias) ---
find /backup/postgres /backup/mongo /backup/logs -type f -mtime +7 -delete

# --- PostgreSQL dump ---
pg_dump -h db -U $POSTGRES_USER -d $POSTGRES_DB | gzip > /backup/postgres/postgres_$(date +%F).sql.gz
mc cp /backup/postgres/postgres_$(date +%F).sql.gz prod/backups/postgres/

# --- MongoDB dump ---
mongodump --uri="mongodb://${MONGO_INITDB_ROOT_USERNAME}:${MONGO_INITDB_ROOT_PASSWORD}@mongodb:27017/${MONGO_DB_DATABASE}?authSource=admin" \
--archive=/backup/mongo/mongo_$(date +%F).gz --gzip
mc cp /backup/mongo/mongo_$(date +%F).gz prod/backups/mongo/

# --- Logs ---
tar czf /backup/logs/logs_$(date +%F).tar.gz -C /data/logs .
mc cp /backup/logs/logs_$(date +%F).tar.gz prod/backups/logs/

echo ">> Backup concluído."

tail -f /dev/null