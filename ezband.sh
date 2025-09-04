#!/bin/bash
set -e

# ----------------------
# Variáveis iniciais
# ----------------------
COMMAND=""
SERVICES=()
TEMP_ENV=".env.temp.$$"  # nome único com PID

# ----------------------
# Função de uso
# ----------------------
usage() {
    echo "Uso: $0 -c {start|stop|restart} [-s service1 -s service2 ...]"
    exit 1
}

# ----------------------
# Parse args
# ----------------------
while [[ $# -gt 0 ]]; do
    case "$1" in
        -c)
            COMMAND="$2"
            shift 2
            ;;
        -s)
            SERVICES+=("$2")
            shift 2
            ;;
        *)
            echo "Argumento desconhecido: $1"
            usage
            ;;
    esac
done

# Comando é obrigatório
if [[ -z "$COMMAND" ]]; then
    echo "Erro: -c é obrigatório"
    usage
fi

# ----------------------
# Função para criar .env temporário seguro
# ----------------------
create_env() {
    if [ ! -f ".env.gpg" ]; then
        echo "Arquivo .env.gpg não encontrado!"
        exit 1
    fi

    echo "Digite a senha do GPG para descriptografar o .env:"
    read -s GPG_PASSPHRASE
    echo

    # Descriptografa para arquivo temporário seguro
    gpg --batch --yes --passphrase "$GPG_PASSPHRASE" -d .env.gpg > "$TEMP_ENV"
    chmod 600 "$TEMP_ENV"

    # Garante que o arquivo seja apagado ao sair
    trap 'rm -f "$TEMP_ENV"' EXIT
}

# ----------------------
# Executa ação
# ----------------------
case "$COMMAND" in
    build)
        if [[ ${#SERVICES[@]} -eq 0 ]]; then
            # Nenhum serviço passado → sobe todos
            docker compose build 
        else
            docker compose build "${SERVICES[@]}"
        fi
        ;;
    start)
        create_env
        if [[ ${#SERVICES[@]} -eq 0 ]]; then
            # Nenhum serviço passado → sobe todos
            docker compose --env-file "$TEMP_ENV" up -d
        else
            docker compose --env-file "$TEMP_ENV" up -d "${SERVICES[@]}"
        fi
        ;;
    stop)
        if [[ ${#SERVICES[@]} -eq 0 ]]; then
            docker compose down
        else
            docker compose stop "${SERVICES[@]}"
        fi
        ;;
    restart)
        create_env
        if [[ ${#SERVICES[@]} -eq 0 ]]; then
            docker compose --env-file "$TEMP_ENV" up -d --build
        else
            docker compose --env-file "$TEMP_ENV" up -d --build "${SERVICES[@]}"
        fi
        ;;
    *)
        echo "Comando inválido: $COMMAND"
        usage
        ;;
esac

