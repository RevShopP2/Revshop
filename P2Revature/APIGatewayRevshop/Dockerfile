FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/APIGatewayRevshop-0.0.1-SNAPSHOT.jar apiGatewayRevshop.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","apiGatewayRevshop.jar"]