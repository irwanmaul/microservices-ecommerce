FROM openjdk:22-jdk

WORKDIR /app

COPY target/api-gateway-1.0-SNAPSHOT.jar /app/api-gateway.jar

ENTRYPOINT ["java","-jar","/app/api-gateway.jar"]