FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app
COPY --from=build /app/target/ExpenseTracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE $PORT

CMD java -Dserver.port=$PORT -jar app.jar