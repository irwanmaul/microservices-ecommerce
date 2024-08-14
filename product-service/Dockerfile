FROM openjdk:22-jdk

WORKDIR /app

COPY target/product-service-1.0-SNAPSHOT.jar /app/product-service.jar

ENTRYPOINT ["java","-jar","/app/product-service.jar"]