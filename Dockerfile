# Fase de construcción
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copiar solo lo necesario para las dependencias primero
COPY pom.xml .
COPY src src

# Instalar Maven y compilar
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests

# Fase de ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/PriorityReservation-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]