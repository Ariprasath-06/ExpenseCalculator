FROM maven:3.9-openjdk-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim

WORKDIR /app
COPY --from=build /app/target/ExpenseTracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE $PORT

CMD java -Dserver.port=$PORT -jar app.jar