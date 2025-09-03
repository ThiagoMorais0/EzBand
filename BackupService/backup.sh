#!/bin/bash
set -e

echo ">> Iniciando container de backup"

# Espera MinIO backup subir
echo '>> Esperando MinIO backup...'
until curl -s http://minio-backup:9000/minio/health/live > /dev/null; do
  echo '>> MinIO ainda não disponível, aguardando...'
  sleep 5
done

# Configurar aliases mc
echo '>> Configurando Aliases MC...'
mc alias set prod http://minio:9000 ${MINIO_ADMIN_USER} ${MINIO_ADMIN_PASSWORD}
mc alias set backup http://minio-backup:9000 ${MINIO_BACKUP_USER} ${MINIO_BACKUP_PASSWORD}

# Diretórios temporários
mkdir -p /backup/postgres /backup/mongo /backup/logs

while true; do
  echo ">> Iniciando backup: $(date)"

  # --- Limpeza de backups antigos (7 dias) ---
  find /backup/postgres /backup/mongo /backup/logs -type f -mtime +7 -delete

  # --- PostgreSQL dump ---
  pg_dump -h db -U $POSTGRES_USER -d $POSTGRES_DB | gzip > /backup/postgres/postgres_$(date +%F).sql.gz
  mc cp /backup/postgres/postgres_$(date +%F).sql.gz backup/backups/postgres/

  # --- MongoDB dump ---
  mongodump --uri="mongodb://${MONGO_INITDB_ROOT_USERNAME}:${MONGO_INITDB_ROOT_PASSWORD}@mongodb:27017/${POSTGRES_DB}?authSource=admin" \
    --archive=/backup/mongo/mongo_$(date +%F).gz --gzip
  mc cp /backup/mongo/mongo_$(date +%F).gz backup/backups/mongo/

  # --- Logs ---
  tar czf /backup/logs/logs_$(date +%F).tar.gz -C /data/logs .
  mc cp /backup/logs/logs_$(date +%F).tar.gz backup/backups/logs/

  # --- MinIO mirror ---
  mc -v mirror --overwrite prod/bandlogos backup/app-bucket/bandlogos

  echo ">> Backup concluído. Próxima execução em 24h."
  sleep 86400
done
