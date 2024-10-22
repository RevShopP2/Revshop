FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/ServiceDiscoveryRevshop-0.0.1-SNAPSHOT.jar serviceDiscoveryRevshop.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","serviceDiscoveryRevshop.jar"]