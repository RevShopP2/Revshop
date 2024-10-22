FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/BuyerMicroService-0.0.1-SNAPSHOT.jar buyerMicroService.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","buyerMicroService.jar"]