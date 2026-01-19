FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8082
CMD ["java","-jar","app.jar"]
