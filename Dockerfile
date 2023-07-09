FROM maven:3.8.2-jdk-11 AS build

COPY . .

RUN mvn clean package

FROM openjdk:11-jdk-slim
COPY --from=build target/property-management-system-0.0.1.jar property-management-system.jar
# ENV PORT=8080
EXPOSE 9091
ENTRYPOINT ["java","-jar","/property-management-system.jar"]
