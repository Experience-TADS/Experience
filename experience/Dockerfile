# =============================================
# Build stage
# =============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom e baixa dependencias primeiro (cache de camadas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o codigo e compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================
# Runtime stage
# =============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o jar gerado
COPY --from=build /app/target/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Inicia com perfil de producao
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
