FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/SellerService-0.0.1-SNAPSHOT.jar seller.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "seller.jar"]