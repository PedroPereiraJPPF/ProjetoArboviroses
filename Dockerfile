# Dockerfile para Backend Java Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar apenas pom.xml primeiro para aproveitar cache do Docker
COPY pom.xml .

# Baixar dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Configurar timezone
RUN ln -sf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone

# Build da aplicação
RUN mvn clean package -DskipTests -B

# Estágio de produção
FROM eclipse-temurin:17-jre-alpine

# Instalar netcat para health checks
RUN apk add --no-cache netcat-openbsd tzdata

# Criar usuário não-root
RUN addgroup -g 1000 spring && \
    adduser -D -u 1000 -G spring spring

WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Configurar timezone
ENV TZ=America/Sao_Paulo

# Mudar propriedade dos arquivos
RUN chown -R spring:spring /app

USER spring

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD nc -z localhost 8080 || exit 1

# Comando para iniciar a aplicação
CMD ["java", "-jar", "-Xms512m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]