# ETAPA 1: BUILD
# Usamos Java 21 para asegurar compatibilidad
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# El cambio clave: -Dmaven.test.skip=true evita que intente compilar los tests de base de datos
RUN mvn clean package -Dmaven.test.skip=true

# ETAPA 2: RUN
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]