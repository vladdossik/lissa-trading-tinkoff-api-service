# Указываем базовый образ с JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл jar в контейнер
COPY target/tinkoff-stock-service-0.0.1-SNAPSHOT.jar /app/service.jar

# Указываем команду запуска приложения
ENTRYPOINT ["java", "-jar", "/app/service.jar"]

# Указываем на каком порту работает приложение
EXPOSE 8082
