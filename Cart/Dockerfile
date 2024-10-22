FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Cart-0.0.1-SNAPSHOT.jar cart.jar

EXPOSE 8003

ENTRYPOINT ["java", "-jar", "cart.jar"]