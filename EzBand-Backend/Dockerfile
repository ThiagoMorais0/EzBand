# Etapa 1: Build do JAR
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Otimização de cache: copia apenas o pom.xml primeiro para baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora copia o código e gera o build
COPY . .
# Substitua a linha 11 por esta:
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -Dfile.encoding=UTF-8

# Etapa 2: Imagem final (Runtime)
# Usamos JRE em vez de JDK para produção porque é muito menor e mais seguro
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Fuso horario: a imagem base nao traz tzdata e a JVM cai em UTC, o que adianta
# em 3h tudo que usa LocalDate/LocalTime.now() (ex.: job que conclui eventos).
RUN apk add --no-cache tzdata
ENV TZ=America/Sao_Paulo

# ffmpeg/ffprobe convertem o VS que o usuario envia (WAV da DAW, em geral) para MP3 antes de
# ir ao storage -- 40MB viram ~5MB. O ffprobe tambem valida que o arquivo tem mesmo faixa de
# audio, o que a extensao sozinha nao garante. ~80MB na imagem final.
RUN apk add --no-cache ffmpeg

# Copia o JAR da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta interna
EXPOSE 8080

# Define variáveis de ambiente padrão caso não venham do compose
ENV SPRING_PROFILES_ACTIVE=docker

# Execução direta para o Docker conseguir gerenciar o processo corretamente
# ... restante do arquivo ...
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]