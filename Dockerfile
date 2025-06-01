# Usa una imagen oficial de Java con Maven preinstalado
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

# Construye la aplicación
RUN mvn clean package -DskipTests

# Imagen final más ligera
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]