FROM maven:3.9.8-eclipse-temurin-21 as builder

WORKDIR /workspace
COPY . .

WORKDIR /workspace/gRPCProtocol
RUN mvn clean install -DskipTests

WORKDIR /workspace/CustomClient
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/CustomClient/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
