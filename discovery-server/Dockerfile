FROM openjdk:22-jdk

WORKDIR /app

COPY target/discovery-server-1.0-SNAPSHOT.jar /app/discovery-server.jar

ENTRYPOINT ["java","-jar","/app/discovery-server.jar"]