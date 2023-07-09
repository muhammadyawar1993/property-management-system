FROM openjdk:11-jdk-slim
ADD target/property-management-system.jar property-management-system.jar
# ENV PORT=8080
EXPOSE 9091
ENTRYPOINT ["java","-jar","/property-management-system.jar"]
