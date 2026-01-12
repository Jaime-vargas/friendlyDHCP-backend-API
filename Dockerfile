FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean install -DskipTests


# --------- Runtime image ---------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/demo.app.gastos-0.1.3-SNAPSHOT.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
