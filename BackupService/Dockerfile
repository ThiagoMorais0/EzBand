FROM alpine:3.22.1

# Instala dependências
RUN apk add --no-cache bash curl gzip tar postgresql-client mongodb-tools \
    && curl -O https://dl.min.io/client/mc/release/linux-amd64/mc \
    && chmod +x mc \
    && mv mc /usr/local/bin/

# Copia o script de backup
COPY backup.sh /usr/local/bin/backup.sh
RUN chmod +x /usr/local/bin/backup.sh

# Ponto de entrada
ENTRYPOINT ["/usr/local/bin/backup.sh"]
