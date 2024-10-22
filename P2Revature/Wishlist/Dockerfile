FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Wishlist-0.0.1-SNAPSHOT.jar wish.jar

EXPOSE 8004

ENTRYPOINT ["java", "-jar", "wish.jar"]