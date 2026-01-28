FROM maven:3.8.4-openjdk-8-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/huobao-drama-1.0.0.jar app.jar
EXPOSE 5678
ENTRYPOINT ["java", "-jar", "app.jar"]
