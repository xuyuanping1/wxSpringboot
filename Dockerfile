FROM openjdk:11-jre-slim
WORKDIR /app
COPY couple-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]
