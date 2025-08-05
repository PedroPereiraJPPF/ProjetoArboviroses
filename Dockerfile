# Dockerfile para Backend Java Spring Boot
FROM maven:3.8-openjdk-17 AS build

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
FROM openjdk:17-jdk-slim

# Instalar netcat para health checks
RUN apt-get update && apt-get install -y netcat-traditional && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root
RUN useradd -r -u 1000 -m -c "spring user" -d /app -s /bin/false spring

WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Configurar timezone
RUN ln -sf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone

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