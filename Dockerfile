# ETAPA 1: BUILD (Compilar el código)
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# ETAPA 2: RUN (Ejecutarlo en una versión ligera de Java)
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]