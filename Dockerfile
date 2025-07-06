

# Этап сборки: используем официальный образ с Gradle и JDK 17
FROM gradle:8.8-jdk17 AS build

WORKDIR /app

# Копируем файлы сборки и исходники
COPY build.gradle settings.gradle ./
COPY src ./src

# Собираем jar с помощью Gradle (bootJar)
RUN gradle bootJar

# Этап запуска: легковесный образ с OpenJDK 17
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar


ENV DEBUG=true
ENV DEBUG_PORT=8003

ENTRYPOINT ["sh", "-c", "if [ \"$DEBUG\" = \"true\" ]; then java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:$DEBUG_PORT -jar app.jar; else java -jar app.jar; fi"]



