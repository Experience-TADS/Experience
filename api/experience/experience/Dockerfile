# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia pom.xml primeiro para aproveitar cache de camadas do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila (sem rodar testes)
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Instala curl (necessário para o HEALTHCHECK) e cria usuário não-root
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Health check compatível com ALB da AWS
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# -XX:+UseContainerSupport  → respeita limites de memória do container ECS
# -XX:MaxRAMPercentage=75.0 → usa no máximo 75% da RAM alocada
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
