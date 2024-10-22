FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/ConfigServerRevshop-0.0.1-SNAPSHOT.jar configServerRevshop.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","configServerRevshop.jar"]
