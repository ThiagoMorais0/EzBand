# Configuração do MinIO para EzBand

## Buckets Necessários

O projeto utiliza **5 buckets** no MinIO:

1. **`profilepictures`** - Fotos de perfil de usuários e membros fantasma
2. **`bandlogos`** - Logos das bandas
3. **`userposts`** - Imagens de publicações (posts de usuários e bandas)
4. **`venuelogo`** - Logos de locais de eventos (venues)
5. **`studiologo`** - Logos de estúdios

---

## Configuração Inicial

### 1. Criar Alias do MinIO

Configure o alias para acessar sua instância local do MinIO:

```bash
mc alias set myminio http://localhost:9000 <MINIO_ADMIN_USER> <MINIO_ADMIN_PASSWORD>
```

**Exemplo:**
```bash
mc alias set myminio http://localhost:9000 minioadmin minioadmin
```

---

## Criar os Buckets

Execute os comandos abaixo para criar todos os buckets:

```bash
mc mb myminio/profilepictures
mc mb myminio/bandlogos
mc mb myminio/userposts
mc mb myminio/venuelogo
mc mb myminio/studiologo
```

---

## Configurar Acesso Público (Anonymous Download)

Para permitir que as imagens sejam acessíveis publicamente via URL direta, configure a política de acesso:

```bash
mc anonymous set download myminio/profilepictures
mc anonymous set download myminio/bandlogos
mc anonymous set download myminio/userposts
mc anonymous set download myminio/venuelogo
mc anonymous set download myminio/studiologo
```

---

## Script Completo (Copie e Cole)

```bash
# 1. Criar alias
mc alias set myminio http://localhost:9000 minioadmin minioadmin

# 2. Criar buckets
mc mb myminio/profilepictures
mc mb myminio/bandlogos
mc mb myminio/userposts
mc mb myminio/venuelogo
mc mb myminio/studiologo

# 3. Configurar acesso público
mc anonymous set download myminio/profilepictures
mc anonymous set download myminio/bandlogos
mc anonymous set download myminio/userposts
mc anonymous set download myminio/venuelogo
mc anonymous set download myminio/studiologo

echo "✅ Configuração do MinIO concluída!"
```

---

## Verificar Configuração

Para verificar se os buckets foram criados corretamente:

```bash
mc ls myminio
```

Para verificar a política de acesso de um bucket específico:

```bash
mc anonymous get myminio/profilepictures
```

---

## Credenciais

As credenciais são configuradas no arquivo `.env`:

```env
minio.user=<MINIO_ADMIN_USER>
minio.password=<MINIO_ADMIN_PASSWORD>
```

E devem corresponder às credenciais usadas ao criar o alias no MinIO Client.

---

## URLs de Acesso

Após o upload, as imagens ficam acessíveis via:

```
http://localhost:9000/{bucket_name}/{file_name}
```

**Exemplo:**
```
http://localhost:9000/profilepictures/12345.jpg
```
