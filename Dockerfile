
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY lombok.config .
COPY src src

RUN mvn install -DskipTests
RUN cp target/*.jar ./live.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/live.jar /app/live.jar

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:InitialRAMPercentage=70 -XX:MinRAMPercentage=70 -XX:MaxRAMPercentage=70"
ENV PROFILE="default"

EXPOSE 8080/tcp
EXPOSE 8081/tcp

CMD java ${JAVA_OPTS} -Dspring.profiles.active=${PROFILE} -jar live.jar
