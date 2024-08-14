FROM openjdk:22-jdk

WORKDIR /app

COPY target/order-service-1.0-SNAPSHOT.jar /app/order-service.jar

ENTRYPOINT ["java","-jar","/app/order-service.jar"]