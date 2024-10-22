FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/productservice-0.0.1-SNAPSHOT.jar product.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "product.jar"]