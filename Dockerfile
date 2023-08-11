# official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/ecom.jar ecom.jar

EXPOSE 8080

CMD ["java", "-jar", "ecom.jar"]