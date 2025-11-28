# ETAPA 1: BUILD
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# --- EL TRUCO DE MAGIA ---
# Limitamos la memoria de Maven a 256MB para que no explote en Render Free
ENV MAVEN_OPTS="-Xmx256m -Xms128m"

# Compilamos saltando tests y forzando UTF-8
RUN mvn clean package -Dmaven.test.skip=true -Dfile.encoding=UTF-8

# ETAPA 2: RUN
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]