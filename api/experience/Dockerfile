# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache de dependências do Docker
COPY experience/pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY experience/src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
# Imagem mínima JRE — reduz surface de ataque e tamanho da imagem
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Cria usuário não-root para execução (boas práticas de segurança AWS)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# Porta da aplicação
EXPOSE 8080

# Health check para uso com AWS ECS / ALB
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Flags JVM otimizadas para containers AWS:
# -XX:+UseContainerSupport  → respeita os limites de memória do container (ECS task)
# -XX:MaxRAMPercentage=75.0 → usa no máximo 75% da RAM alocada ao container
# -Dspring.profiles.active  → usa perfil de produção por padrão
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
