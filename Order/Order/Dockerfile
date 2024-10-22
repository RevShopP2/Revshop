FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Order-0.0.1-SNAPSHOT.jar order.jar

EXPOSE 8005

ENTRYPOINT ["java", "-jar", "order.jar"]